package com.code.jamie.droid_ussd.controller;

import com.africastalking.AfricasTalking;
import com.africastalking.SmsService;
import com.code.jamie.droid_ussd.domain.User;
import com.code.jamie.droid_ussd.domain.Vaccination;
import com.code.jamie.droid_ussd.service.VaccineService;
import com.elarian.ConnectionListener;
import com.elarian.Customer;
import com.elarian.Elarian;
import com.elarian.ICustomer;
import com.elarian.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController(value = "/")
@RequiredArgsConstructor
public class VaccineController {
    private final VaccineService vaccineService;
    private static Elarian elarian;
    static {
    elarian = new Elarian("el_k_test_a1d6a7b64ce74a9739d116db38f3f39ddd9c774e4107d45c83416fbb50606e92",
            "el_org_eu_WetuZC", "el_app_R6C6Yk");
    }

    @PostMapping
    public ResponseEntity<?> ussdCallback(@RequestParam(value = "sessionId") String sessionId,
                                          @RequestParam(value = "serviceCode") String serviceCode,
                                          @RequestParam(value = "phoneNumber") String phoneNumber,
                                          @RequestParam(value = "text") String text) throws IOException {
        AfricasTalking.initialize("sandbox", "56ec540e4a8c2976082f2d3cc8c60e9c398ed46c166e357b50ee301d0eb59a06");

        System.out.println(text);
        if (Objects.equals(text, "")) {
            return ResponseEntity.status(HttpStatus.OK).body("CON Welcome to Smart Immuno\n" +
                    "1. Register\n2. Login");
        } else if (Objects.equals(text, "1")) {
            User userByPhone = vaccineService.findUserByPhone(phoneNumber);
            if (userByPhone != null) {
                return ResponseEntity.status(HttpStatus.OK).body("END Smart Immuno\nUser already exist");
            } else {
                StringBuilder pin = new StringBuilder();
                for (int i = 0; i <= 3; i++) {
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
        } else if (text.split("\\*").length == 2 && text.startsWith("2*")) {
            String[] textString = text.split("\\*");
            String pin = textString[1];
            User user = vaccineService.findUserByPhone(phoneNumber);
            if (Objects.equals(user.getPin(), pin)) {
                return ResponseEntity.status(HttpStatus.OK).body("CON Smart Immuno\n1. Get my Immunizations\n2. Add new Vaccination");
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("END Incorrect credentials.");
            }
        } else if (text.split("\\*").length == 3 && text.startsWith("2*") && text.endsWith("*1")) {
            StringBuilder msg = new StringBuilder();
            List<Vaccination> vaccinationList = vaccineService.search(phoneNumber);
            for (Vaccination v : vaccinationList) {
                msg.append(v.getVaccineName()).append("\n ");
                int len = v.getVaccineDates().size();
                for (int i = 0; i < len; i++) {
                    msg.append("Dose: ").append(i).append(SimpleDateFormat.getDateInstance().format(v.getVaccineDates().get(i))).append("\n");
                }
            }
            if (vaccinationList.isEmpty()) {
                return ResponseEntity.status(HttpStatus.OK).body("END You have no immunizations");
            }
            SmsService service = AfricasTalking.getService(AfricasTalking.SERVICE_SMS);
            service.send(msg.toString(), new String[]{phoneNumber}, true);
            return ResponseEntity.status(HttpStatus.OK).body("END You will receive notification soon.");
        } else if (text.split("\\*").length == 3 && text
                .startsWith("2*") && text.endsWith("*2")) {
            return ResponseEntity.status(HttpStatus.OK).body("CON Smart Immuno\nEnter Vaccine name");
        } else if (text.split("\\*").length == 4 && text.startsWith("2*")) {
            return ResponseEntity.status(HttpStatus.OK).body("CON Smart Immuno\nEnter first dosage date eg.dd/MM/yyyy");
        } else if (text.split("\\*").length == 5 && text.startsWith("2*")) {
            String[] reqStr = text.split("\\*");
            String name = reqStr[reqStr.length - 2];
            String date = reqStr[reqStr.length - 1];
            getVaccine(phoneNumber, date, name);
//            elarianClient.setOnReminderNotificationHandler(new NotificationHandler<ReminderNotification>() {
//                @Override
//                public void handle(ReminderNotification reminderNotification, ICustomer iCustomer, DataValue dataValue, NotificationCallback<Message> notificationCallback) {
//                    System.out.println("Received reminder notif...");
//                }
//            });
//            elarianClient.connect(new ConnectionListener() {
//                @Override
//                public void onPending() {
//
//                }
//
//                @Override
//                public void onConnecting() {
//
//                }
//
//                @Override
//                public void onClosed() {
//
//                }
//
//                @Override
//                public void onConnected() {
//                    System.out.println("Connected to notif service...");
//                    Customer cus = new Customer(elarianClient, new CustomerNumber(phoneNumber.toString(), CustomerNumber.Provider.CELLULAR));
//
//                }
//
//                @Override
//                public void onError(Throwable throwable) {
//
//                }
//            });
            return ResponseEntity.status(HttpStatus.OK).body("END Vaccine added successfully");
        } else {
            System.out.println(text);
            return ResponseEntity.status(HttpStatus.OK).body("END Session ended");
        }
    }

//    Elarian elarianClient = new Elarian("el_k_test_a1d6a7b64ce74a9739d116db38f3f39ddd9c774e4107d45c83416fbb50606e92",
//            "el_org_eu_WetuZC", "el_app_R6C6Yk");

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
//                elarian.setOnReminderNotificationHandler((reminderNotification, iCustomer, dataValue, notificationCallback) -> System.out.println("Received reminder notif..."));
//                int finalI = i;
//
//                elarian.connect(new ConnectionListener() {
//                    @Override
//                    public void onPending() {
//
//                    }
//
//                    @Override
//                    public void onConnecting() {
//
//                    }
//
//                    @Override
//                    public void onClosed() {
//
//                    }
//
//                    @Override
//                    public void onConnected() {
//                        System.out.println("Connected to notif service...");
//                        Customer cus = new Customer(elarian, new CustomerNumber(phone.toString(), CustomerNumber.Provider.CELLULAR));
//                        cus.addReminder(
//                                new Reminder(
//                                        "Smart Immuno",
//                                        "Your next " + name + " is on " + dates.get(finalI),
//                                        dates.get(finalI)
//                                )
//                        ).subscribe(
//                                System.out::println,
//                                Throwable::printStackTrace
//                        );
//                    }
//
//                    @Override
//                    public void onError(Throwable throwable) {
//
//                    }
//                });
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
