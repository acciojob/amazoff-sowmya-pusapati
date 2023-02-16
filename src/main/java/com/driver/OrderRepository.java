package com.driver;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

@Repository
public class OrderRepository {
    private HashMap<String,Order> orderDB;
    private HashMap<String,DeliveryPartner> partnerDB;
    private HashMap<String, HashSet<String>> PartnerToOrderDB;
    private HashMap<String,String> OrderToPartnerDB;

    public OrderRepository() {
        this.orderDB=new HashMap<>();
        this.partnerDB=new HashMap<>();
        this.PartnerToOrderDB=new HashMap<>();
        this.OrderToPartnerDB=new HashMap<>();
    }

    public void addOrder(Order order)
    {
       String orderid=order.getId();

           orderDB.put(orderid, order);


    }
    public void addPartner(String partnerId)
    {
        DeliveryPartner partner=new DeliveryPartner(partnerId);

            partnerDB.put(partnerId, partner);

    }
    public void addOrderPartnerPair(String orderId,String partnerId)
    {
        if(orderDB.containsKey(orderId)&&partnerDB.containsKey(partnerId))
        {
            HashSet<String> currentorders=new HashSet<>();
            if(PartnerToOrderDB.containsKey(partnerId))
                currentorders=PartnerToOrderDB.get(partnerId);
            currentorders.add(orderId);
            PartnerToOrderDB.put(partnerId,currentorders);

            DeliveryPartner deliveryPartner=new DeliveryPartner(partnerId);
            deliveryPartner.setNumberOfOrders(currentorders.size());

            OrderToPartnerDB.put(orderId,partnerId);
        }
    }
    public Order getOrderById(String orderid)
    {
        if(orderDB.containsKey(orderid))
        {
            return orderDB.get(orderid);
        }
        return null;
    }
    public DeliveryPartner getPartnerById(String partnerId)
    {
        if(partnerDB.containsKey(partnerId))
        {
            return partnerDB.get(partnerId);
        }
        return null;
    }
    public Integer getOrderCountByPartnerId(String partnerId)
    {
        Integer ordercount=0;
        if(partnerDB.containsKey(partnerId))
        {
            ordercount=partnerDB.get(partnerId).getNumberOfOrders();
        }
        return ordercount;
    }
    public List<String> getOrdersByPartnerId(String partnerId)
    {
        HashSet<String> orders=new HashSet<>();
        if(PartnerToOrderDB.containsKey(partnerId))
        {
            orders=PartnerToOrderDB.get(partnerId);
        }
        return new ArrayList<>(orders);

    }
    public List<String> getAllOrders()
    {
        return new ArrayList<>(orderDB.keySet());
    }
    public Integer getCountOfUnassignedOrders()
    {
        Integer totalorders=orderDB.size();
        Integer assignedOrders=OrderToPartnerDB.size();
        return totalorders-assignedOrders;

    }
    public Integer getOrdersLeftAfterGivenTimeByPartnerId(String time,String partnerId)
    {
        Integer hour=Integer.valueOf(time.substring(0,2));
        Integer minutes=Integer.valueOf(time.substring(3));
        Integer timeT=hour *60 + minutes;
        Integer orderscount=0;
        if(PartnerToOrderDB.containsKey(partnerId)) {
            HashSet<String> orders = PartnerToOrderDB.get(partnerId);
            for(String order:orders)
            {
                if(orderDB.containsKey(order))
                {
                    Order currorder=orderDB.get(order);
                    if(timeT<currorder.getDeliveryTime())
                    {

                        orderscount++;
                    }
                }
            }


        }
        return orderscount;
    }
    public String  getLastDeliveryTimeByPartnerId(String patnerId)
    {
        int time=0;
        if(PartnerToOrderDB.containsKey(patnerId))
        {
            HashSet<String> orders=PartnerToOrderDB.get(patnerId);
            for(String order:orders)
            {
                if(orderDB.containsKey(order))
                {
                    time=Math.max(time,orderDB.get(order).getDeliveryTime());
                }
            }
        }

        Integer hour=time/60;
        Integer minutes=time%60;

        String hourinString=String.valueOf(hour);
        String minutesinString=String.valueOf(minutes);
        if(hourinString.length()==1)
        {
            hourinString="0"+hourinString;
        }
        if(minutesinString.length()==1)
        {
            minutesinString="0"+minutesinString;
        }
        return hourinString+":"+minutesinString;
    }
       public void deletePartnerById(String partnerId)
       {
           HashSet<String> orders=new HashSet<>();
           if(PartnerToOrderDB.containsKey(partnerId))
           {
               orders=PartnerToOrderDB.get(partnerId);
               for(String order:orders)
               {
                   if(OrderToPartnerDB.containsKey(order))
                   {
                       OrderToPartnerDB.remove(order);
                   }
               }

               PartnerToOrderDB.remove(partnerId);
           }
           if(partnerDB.containsKey(partnerId)) {
               partnerDB.remove(partnerId);
           }

       }
       public void deleteOrderById(String orderId)
       {
           if(OrderToPartnerDB.containsKey(orderId))
           {
               String partnerid=OrderToPartnerDB.get(orderId);
               HashSet<String> orders=PartnerToOrderDB.get(partnerid);
               orders.remove(orderId);
               PartnerToOrderDB.put(partnerid,orders);

               DeliveryPartner deliveryPartner=new DeliveryPartner(partnerid);
               deliveryPartner.setNumberOfOrders(orders.size());
           }
           if(orderDB.containsKey(orderId))
           {
               orderDB.remove(orderId);
           }
       }
}
