package com.leyou.goods.listener;

import com.leyou.goods.service.GoodsHtmlService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GoodsListener {

    @Autowired
    private GoodsHtmlService goodsHtmlService;

    @RabbitListener(bindings = @QueueBinding(    //绑定消息队列
            value = @Queue(value = "LEYOU.ITEM.SAVE.QUEUE",durable = "true"),//绑定持久化的队列LEYOU.ITEM.SAVE.QUEUE ,持久化此队列
            exchange = @Exchange(value = "LEYOU.ITEM.EXCHANGE",ignoreDeclarationExceptions = "true",type = ExchangeTypes.TOPIC), //接收此交换机中的消息,忽略声明式异常,发布订阅模式的通配符类型
            key = {"item.insert","item.update"}  //指定匹配接收的路由key
    ))
    public void save(Long id){
        if(id==null){
            return;
        }
        goodsHtmlService.createHtml(id);
    }


    @RabbitListener(bindings = @QueueBinding(    //绑定消息队列
            value = @Queue(value = "LEYOU.ITEM.DELETE.QUEUE",durable = "true"),//绑定持久化的队列LEYOU.ITEM.SAVE.QUEUE ,持久化此队列
            exchange = @Exchange(value = "LEYOU.ITEM.EXCHANGE",ignoreDeclarationExceptions = "true",type = ExchangeTypes.TOPIC), //接收此交换机中的消息,忽略声明式异常,发布订阅模式的通配符类型
            key = {"item.delete"}  //指定匹配接收的路由key
    ))
    public void delete(Long id){
        if(id==null){
            return;
        }
        goodsHtmlService.deleteHtml(id);
    }

}
