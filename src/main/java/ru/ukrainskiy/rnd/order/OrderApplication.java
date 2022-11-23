package ru.ukrainskiy.rnd.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);

        OrderService orderService = new OrderService();

        long start = System.currentTimeMillis();
        for (long i = 0; i < 10000000; i++) {
            Order order = new Order();
            order.setId(i);
            orderService.addOrder(order);
        }
        long finish = System.currentTimeMillis() - start;
        System.out.println("Время заполнения 10_000_000 ордеров: " + finish);

        start = System.currentTimeMillis();
        for (long i = 5673089; i < 5673089+20000; i++) {
            Order order = new Order();
            order.setId(i);
            orderService.removeOrder(order);
        }
        finish = System.currentTimeMillis() - start;
        System.out.println("Время удаления 20_000 ордеров: " + finish);

        start = System.currentTimeMillis();
        orderService.lastOrder100().forEach(o -> System.out.println(o.getId()));
        finish = System.currentTimeMillis() - start;
        System.out.println("Время вывода последних 100 добавленых ордеров: " + finish);
    }

}
