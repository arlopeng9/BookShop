package com.bookshop.controller;

import com.bookshop.common.responseFromServer;
import com.bookshop.dao.OrderDao;
import com.bookshop.entity.Book;
import com.bookshop.entity.Order;
import com.bookshop.entity.OrderItem;
import com.bookshop.entity.User;
import com.bookshop.service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: OrderController
 * @Description:
 * @Author: 曾志昊
 * @Date: 2020/3/28 1:47
 */
@Controller
@RequestMapping("/order")
public class OrderController{

    @Resource
    OrderService orderService;

    @RequestMapping("/getOrder")
    @ResponseBody
    public responseFromServer getOrder(@RequestBody Long orderId){
        return orderService.getOrder(orderId);
    }

    @RequestMapping("/getOrdersByUserId")
    @ResponseBody
    public responseFromServer getOrdersByUserId(@RequestBody Long userId){
        return orderService.getOrdersByUserId(userId);
    }


    @RequestMapping("/searchOrdersPage")
    @ResponseBody
    public responseFromServer searchOrderPage(@RequestBody Map<String,Object> requestMap){
        return orderService.searchOrdersPage(requestMap);
    }

/*
    @RequestMapping("/createOrder")
    @ResponseBody
    public responseFromServer createOrder(@RequestBody )*/

    @RequestMapping("/insertOrder")
    @ResponseBody
    public responseFromServer insertOrder(@RequestBody Order order,HttpSession session){
        if(order.getUser()==null&&order.getUserId()==null){
            User tempUser = (User)session.getAttribute("user");
            order.setUserId(tempUser.getUserId());
        }

        return orderService.insertOrder(order);
    }

    @RequestMapping("/insertOrders")
    @ResponseBody
    public responseFromServer insertOrders(@RequestBody List<Order> orders){
        return orderService.insertOrders(orders);
    }


    @RequestMapping("/getOrdersPlusByUserId")
    @ResponseBody
    public responseFromServer getOrdersPlusByUserId(@RequestBody Map<String,Object>reqeustMap){
        //requestMap中应包含userId
        return orderService.getOrdersPlusByUserId(reqeustMap);
    }

    @RequestMapping("/updateOrder")
    @ResponseBody
    public responseFromServer updateOrder(@RequestBody Order order){
        return orderService.updateOrder(order);
    }

    @RequestMapping("/updateOrders")
    @ResponseBody
    public responseFromServer updateOrders(@RequestBody List<Order> orders){
        return orderService.updateOrders(orders);
    }

    @RequestMapping("/deleteOrderById")
    @ResponseBody
    public responseFromServer deleteOrderById(@RequestBody Long orderId){
        return orderService.deleteOrderById(orderId);
    }

    @RequestMapping("/deleteOrder")
    @ResponseBody
    public responseFromServer deleteOrder(@RequestBody Order order){
        return orderService.deleteOrder(order);
    }

    @RequestMapping("/deleteOrders")
    @ResponseBody
    public responseFromServer deleteOrders(@RequestBody List<Order> orders){
        return orderService.deleteOrders(orders);
    }

    @RequestMapping("/showItems")
    @ResponseBody
    public responseFromServer showItems(@RequestBody OrderItem item){
        Book book = item.getBook();
        return responseFromServer.success(book);
    }

    @RequestMapping("/insertOrderItem")
    @ResponseBody
    public responseFromServer insertOrderItem(OrderItem orderItem){
        return orderService.insertOrderItem(orderItem);
    }


    @RequestMapping("/updateOrderItem")
    @ResponseBody
    public responseFromServer updateOrderItem(List<OrderItem> orderItems){
        return orderService.updateOrderItem(orderItems);
    }



    @RequestMapping("/deleteOrderItem")
    @ResponseBody
    public responseFromServer deleteOrderItem(OrderItem orderItem){
        return orderService.deleteOrderItem(orderItem);
    }

}
