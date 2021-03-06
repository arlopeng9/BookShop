package com.bookshop.service.serviceImpl;

import com.bookshop.common.responseFromServer;
import com.bookshop.dao.OrderDao;
import com.bookshop.dao.OrderItemDao;
import com.bookshop.dao.UserDao;
import com.bookshop.entity.Order;
import com.bookshop.entity.OrderItem;
import com.bookshop.entity.User;
import com.bookshop.service.OrderService;
import com.bookshop.util.Page;
import com.bookshop.util.configs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: OrderServiceImpl
 * @Description: OrderService 同时包含了对OrderItems的操作
 * @Author: 曾志昊
 * @Date: 2020/3/28 2:02
 */
@Service(value = "OrderService")
public class OrderServiceImpl implements OrderService {

    UserDao userDao;
    OrderDao orderDao;
    OrderItemDao orderItemDao;

    public responseFromServer getOrder(Long orderId){
        Order order = orderDao.getOrder(orderId);
        if(order!=null)
            return responseFromServer.success(orderDao.getOrder(orderId));
        else
            return responseFromServer.error("不存在该订单");
    }

    public responseFromServer getOrdersByUserId(Long userId){
        if(userDao.getUser(userId)==null){
            return responseFromServer.error("不存在该用户");
        }else{
            List<Order> orders = orderDao.getOrdersByUserId(userId);
            return responseFromServer.success(orders);
        }
    }

    public responseFromServer searchOrdersPage(Map<String,Object> queryMap){
/*        Page<Order> page = new Page<Order>(configs.pageSize);
        queryMap.put("pageSize",configs.pageSize);
        page.setTotalCount(orderDao.count(queryMap));
        page.setCurrPage((Integer)queryMap.get("startPage")+1);
        page.setTotalPage(((Double)Math.ceil(page.getTotalCount()/configs.pageSize)).intValue());
        page.setLists(orderDao.searchOrders(queryMap));
        return responseFromServer.success(page);
        */
        return responseFromServer.success(getPage(queryMap));
    }

    public responseFromServer getOrdersPlusByUserId(Map<String,Object>requestMap){
//        TODO：完成plus
        if(userDao.getUser((Long)requestMap.get("userId"))==null){
            return responseFromServer.error("不存在该用户");
        }else{
            /*List<Order> orders = orderDao.getOrdersPlusByUserId(userId);
            return responseFromServer.success(orders);*/
            Map<String,Object> queryMap = new HashMap<>();
            queryMap.put("userId",requestMap.get("userId"));
            queryMap.put("startPage",(Integer)requestMap.get("startPage"));
            return responseFromServer.success(getPage(queryMap));
        }
    }

    public Page getPage(Map<String,Object> queryMap){
        Page<Order> page = new Page<Order>(configs.pageSize);
        queryMap.put("pageSize",configs.pageSize);
        page.setTotalCount(orderDao.count(queryMap));
        page.setCurrPage((Integer)queryMap.get("startPage"));
        queryMap.put("startPage",page.getCurrPage()-1);
        page.setTotalPage(((Double)Math.ceil(page.getTotalCount()/configs.pageSize)).intValue());
        page.setLists(orderDao.searchOrders(queryMap));
        return page;
    }

    @Transactional
    public responseFromServer insertOrder(Order order){
        if(order == null
                ||order.getOrderId()==null
                ||order.getUserId()==null
                ||order.getBooks()==null
                ||order.getBooks().size()==0){
            return responseFromServer.error("创建订单失败");
        }else if(order.getOrderAddress()==null){
            /*使用默认地址*/
            if(order.getUser()!=null&&order.getUser().getUserAddress()!=null){
                order.setOrderAddress(order.getUser().getUserAddress());
            }else{
                /*查询默认地址*/
                User user = userDao.getUser(order.getUserId());
                String address = user.getUserAddress();
                if (address == null || address == "") {
                    responseFromServer.error("未设置默认地址");
                }
                order.setUser(user);
                order.setOrderAddress(address);
            }
        }else if(order.getPrice()==null||order.getPrice()==0){
            /*检查价格*/
            order.calculateTotalPrice();
        }
        if(orderDao.insertOrder(order)!=1){
            return responseFromServer.error("创建订单失败");
        }else{
            /*插入orderItem*/
            /*插入order后生成了orderid 将orderid更新到每个orderItem中*/
            List<OrderItem> orderItems = order.getBooks();
            for(int i =0;i<orderItems.size();i++){
                orderItems.get(i).setOrderId(order.getOrderId());
            }
            int affectedRows = orderItemDao.insertOrderItems(orderItems);
            if(affectedRows<orderItems.size()){
                /*如果插入订单失败 手动回滚事务*/
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return responseFromServer.error("创建订单失败，"+affectedRows+"(/"+orderItems.size()+")个商品插入成功");
            }
            order.setBooks(orderItems);
            order.setOrderStatus("orderUnpaid");
            return responseFromServer.success("创建订单成功",order);
        }
    }

    /**
     * @Description: 计算还未插入的订单的价格
     * @Date:   2020/4/1 17:04
     */
    public responseFromServer calculateTotalPrice(Order order){
        if(order==null)return responseFromServer.error("订单为空");
        Double price = order.calculateTotalPrice();
        if(price<0){
            return responseFromServer.error("订单信息错误");
            /*此时存在orderItem对象内有book对象缺失*//*
            responseFromServer<Order> subResponse = getOrder(order.getOrderId());
            if(subResponse.isSuccess()){
                *//*根据传进的orderId来查询order完整信息，再*//*
                order = (Order) subResponse.getData();
                return responseFromServer.success(order.calculateTotalPrice());
            }else{
                return responseFromServer.error("订单信息错误");
            }*/
        }else{
            return responseFromServer.success(price);
        }
    }

    public responseFromServer insertOrders(List<Order> orders){
        if (orders == null || orders.size()==0){
            return responseFromServer.error("插入订单列表为空");
        }else{
            int rows = 0;
            for(Order order:orders){
                if(this.insertOrder(order).isSuccess()){
                    rows++;
                }
            }
            if(rows<orders.size()){
                return responseFromServer.error("成功插入了"+rows+"(/"+orders.size()+")个订单");
            }else{
                return responseFromServer.success("成功插入所有订单");
            }
        }
    }

    @Transactional
    public responseFromServer updateOrder(Order order){
        if(order==null){
            return responseFromServer.error("订单信息错误");
        }else if(order.calculateTotalPrice()<0){
            /*计算价格错误*/
            return responseFromServer.error("订单商品信息不足");
        }
        if(orderDao.updateOrder(order)!=1||order.getOrderId()==null){
            return responseFromServer.error("更新订单失败");
        }else{
            Long orderId = order.getOrderId();
            /*首先更新order基本信息：地址 状态等*/
            /*然后更新商品数量,多的删除,少的增加 直接把整个订单item全部删除再全部添加*/
            if(!deleteOrderItemsByOrderId(orderId).isSuccess()){
                return responseFromServer.error("待更新orderid为空");
            }
            int affectedRows = 0;
            List<OrderItem> orderItems = order.getBooks();
            for(OrderItem orderItem:orderItems){
                orderItem.setOrderId(orderId);
                if(insertOrderItem(orderItem).isSuccess()){
                    affectedRows++;
                }
            }
            if(affectedRows<orderItems.size()){
                /*如果插入订单失败 手动回滚事务*/
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return responseFromServer.error("更新订单失败");
            }
            order.setBooks(orderItems);
            order.setOrderStatus("orderUnpaid");
            return responseFromServer.success("创建订单成功",order);
        }
    }


    public responseFromServer updateOrders(List<Order> orders){
        if(orders==null||orders.size()==0)
            return responseFromServer.error("更新订单列表为空");
        int rows = 0;
        for(Order order:orders){
            if(orderDao.updateOrder(order)==1){
                rows++;
            }
        }
        if(rows<orders.size()){
            return responseFromServer.error("成功更新了"+rows+"(/"+orders.size()+")个订单");
        }else{
            return responseFromServer.success("成功更新所有订单");
        }
    }

    public responseFromServer deleteOrderById(Long orderId){
        if(orderId == null || orderDao.deleteOrderById(orderId)!=1)
            return responseFromServer.error("删除订单失败");
        else
            return responseFromServer.success("成功订单删除");
    }

    public responseFromServer deleteOrder(Order order){
        if(order==null||orderDao.deleteOrderById(order.getOrderId())!=1){
            return responseFromServer.error("删除订单失败");
        }else{
            return responseFromServer.success("删除订单成功");
        }
    }

    public responseFromServer deleteOrders(List<Order> orders){
        if(orders == null||orders.size()==0){
            return responseFromServer.error("删除订单列表为空");
        }else{
            int rows = 0;
            for(Order order:orders){
                if(orderDao.deleteOrderById(order.getOrderId())==1){
                    rows++;
                }
            }
            if(rows<orders.size()){
                return responseFromServer.error("成功删除了"+rows+"(/"+orders.size()+")个订单");
            }else{
                return responseFromServer.success("成功删除所有订单");
            }
        }
    }


    public responseFromServer insertOrderItem(OrderItem orderItem){
        if(orderItem.getOrderId()==null
                ||orderItem.getBookId()==null
                ||orderItemDao.insertOrderItem(orderItem)==1){
            return responseFromServer.success("成功插入orderItem");
        }else{
            return responseFromServer.error();
        }
    }


    public responseFromServer updateOrderItem(OrderItem orderItem){
        if(orderItemDao.updateOrderItem(orderItem)!=1){
            return responseFromServer.error("更新item失败");
        }else{
            return responseFromServer.success();
        }
    }

    public responseFromServer updateOrderItem(List<OrderItem> orderItems){
        Integer rows = orderItemDao.updateOrderItems(orderItems);
        if(rows<orderItems.size()){
            return responseFromServer.error(String.format("更新了%d(/%d)",rows,orderItems.size()));
        }else{
            return responseFromServer.success("全部更新成功");
        }
    }

    public responseFromServer deleteOrderItem(OrderItem orderItem){
        if(orderItemDao.deleteOrderItem(orderItem)!=1){
            return responseFromServer.error("删除orderItem失败");
        }
        return responseFromServer.success("删除orderItem成功");
    }

    public responseFromServer deleteOrderItemsByOrderId(Long orderId){
        if(orderId==null) return responseFromServer.error("要删除的orderId为空");
        Integer rows = orderItemDao.deleteOrderItemsByOrderId(orderId);
        return responseFromServer.success(rows);
    }



    @Autowired
    public OrderServiceImpl(OrderDao orderDao, OrderItemDao orderItemDao,UserDao userDao){
        this.userDao = userDao;
        this.orderDao = orderDao;
        this.orderItemDao = orderItemDao;
    }



}
