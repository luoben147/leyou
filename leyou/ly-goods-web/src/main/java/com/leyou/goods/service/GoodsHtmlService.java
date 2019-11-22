package com.leyou.goods.service;

import com.leyou.goods.utils.ThreadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 * 生成静态页面
 */
@Service
public class GoodsHtmlService {

    @Autowired
    private TemplateEngine engine;

    @Autowired
    private GoodsService goodsService;

    public void createHtml(Long spuId){

        //初始化Context
        Context context = new Context();
        //设置数据模型
        context.setVariables(goodsService.loadData(spuId));
        PrintWriter printWriter=null;
        try {
            //静态文件生成到服务器本地
            File file = new File("D:\\tools\\nginx-1.14.0\\html\\item\\" + spuId + ".html");
            printWriter = new PrintWriter(file);

            engine.process("item",context,printWriter);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }finally {
            if(printWriter!=null){
                printWriter.close();
            }
        }
    }

    /**
     * 新建线程处理页面静态化
     * @param spuId
     */
    public void asyncExcute(Long spuId) {
        ThreadUtils.execute(()->createHtml(spuId));
        /*ThreadUtils.execute(new Runnable() {
            @Override
            public void run() {
                createHtml(spuId);
            }
        });*/
    }

    public void deleteHtml(Long id) {
        File file = new File("D:\\tools\\nginx-1.14.0\\html\\item\\" + id + ".html");
        file.deleteOnExit();
    }
}
