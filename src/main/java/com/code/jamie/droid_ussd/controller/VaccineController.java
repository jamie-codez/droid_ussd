package com.code.jamie.droid_ussd.controller;

import com.africastalking.AfricasTalking;
import com.africastalking.SmsService;
import com.code.jamie.droid_ussd.domain.User;
import com.code.jamie.droid_ussd.domain.Vaccination;
import com.code.jamie.droid_ussd.service.VaccineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

@RestController(value = "/")
@RequiredArgsConstructor
public class VaccineController {
    private final VaccineService vaccineService;

    @PostMapping
    public ResponseEntity<?> ussdCallback(@RequestParam(value = "sessionId") String sessionId,
                                          @RequestParam(value = "serviceCode") String serviceCode,
                                          @RequestParam(value = "phoneNumber") String phoneNumber,
                                          @RequestParam(value = "text") String text) throws IOException {
        AfricasTalking.initialize("sandbox", "b8cd46b27196183f859ef0a454abce5dc9cbb5001dc040c2ac6c7958e84bd1a0");
        if (Objects.equals(text, "")) {
            return ResponseEntity.status(HttpStatus.OK).body("CON Welcome to Smart Immuno\n" +
                    "1. Register\n2. Login");
        } else if (Objects.equals(text, "1")) {
            User userByPhone = vaccineService.findUserByPhone(phoneNumber);
            if (userByPhone != null) {
                return ResponseEntity.status(HttpStatus.OK).body("END Smart Immuno\nUser already exist");
            } else {
                StringBuilder pin = new StringBuilder();
                for (int i = 0; i < 4; i++) {
                    pin.append(Math.round(Math.random() * 10));
                }
                vaccineService.saveUser(new User(null, phoneNumber, pin.toString()));
                SmsService smsService = AfricasTalking.getService(AfricasTalking.SERVICE_SMS);
                smsService.send("Dear customer you have successfully registered for Smart Immuno." +
                        "Your pin is " + pin + ".This info is confidential.\n Welcome to the team.🎉", new String[]{phoneNumber}, true);
                return ResponseEntity.status(HttpStatus.OK).body("END Smart Immuno\nSuccessfully registered\nInitiate login");
            }
        } else if (Objects.equals(text, "2")) {
            return ResponseEntity.status(HttpStatus.OK).body("CON Smart Immuno\nEnter pin");
        } else if (text.startsWith("2*")) {
            String[] textString = text.split("\\*");
            String pin = textString[1];
            User user = vaccineService.findUserByPhone(phoneNumber);
            if (Objects.equals(user.getPin(), pin)) {
                return ResponseEntity.status(HttpStatus.OK).body("CON Smart Immuno\n1. Get my Immunizations\n2. Add new Vaccination");
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("END Incorrect credentials.");
            }
        } else if (Pattern.matches("2*[0-9],[0-9],[0-9],[0-9]*1", text)) {
            StringBuilder msg = new StringBuilder();
            List<Vaccination> vaccinationList = vaccineService.search(phoneNumber);
            for (Vaccination v : vaccinationList) {
                msg.append(v.getVaccineName()).append("\n");
                v.getVaccineDates().forEach(date -> {
                    msg.append(SimpleDateFormat.getDateInstance().format(date)).append("\n");
                });
            }
            if (vaccinationList.isEmpty()) {
                return ResponseEntity.status(HttpStatus.OK).body("END You have no immunizations");
            }
            SmsService service = AfricasTalking.getService(AfricasTalking.SERVICE_SMS);
            service.send(msg.toString(), new String[]{phoneNumber}, true);
            return ResponseEntity.status(HttpStatus.OK).body("END You will receive notification soon.");
        } else if (Objects.equals(text, "2*\\[0-9,0-9,0-9,0-9]*2")) {
            return ResponseEntity.status(HttpStatus.OK).body("CON Smart Immuno\nEnter Vaccine name");
        } else if (text.startsWith("2*\\[0-9,0-9,0-9,0-9]*1*\\[A-Z,a-z]")) {
            return ResponseEntity.status(HttpStatus.OK).body("CON Smart Immuno\nEnter first dosage date eg.dd/MM/yyyy");
        } else if (text.startsWith("2*\\[0-9,0-9,0-9,0-9]*2*\\[A-Z,a-z]*[0-9],[0-9]/[0-9],[0-9]/[0-9],[0-9],[0-9],[0-9]")) {
            String[] reqStr = text.split("\\*");
            String name = reqStr[reqStr.length - 2];
            String date = reqStr[reqStr.length - 1];
            getVaccine(phoneNumber, date, name);
            return ResponseEntity.status(HttpStatus.OK).body("END Vaccine added successfully");
        } else {
            return ResponseEntity.status(HttpStatus.OK).body("END Session ended");
        }
    }

    public void getVaccine(String phone, String dateStr, String name) {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        long timeMillis = 0;
        try {
            Date date = format.parse(dateStr);
            timeMillis = date.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<Long> dates = new ArrayList<>();
        dates.add(timeMillis);
        if (name.toLowerCase(Locale.getDefault()).equals("polio")) {
            for (int i = 0; i < 5; i++) {
                long period = 1000L * 60 * 60 * 24 * 30 * 2;
                dates.add(dates.get(i) + period);
            }
        }
        if (name.toLowerCase(Locale.getDefault()).equals("hepatitis")) {
            for (int i = 0; i < 5; i++) {
                long period = 1000L * 60 * 60 * 24 * 30 * 6;
                dates.add(dates.get(i) + period);
            }
        }
        vaccineService.saveVaccine(new Vaccination(null, name, phone, dates));
    }

}
