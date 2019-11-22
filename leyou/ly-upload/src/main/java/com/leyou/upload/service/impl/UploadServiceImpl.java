package com.leyou.upload.service.impl;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.leyou.upload.service.IUploadService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
public class UploadServiceImpl implements IUploadService {

    private Logger logger=LoggerFactory.getLogger(UploadServiceImpl.class);

    private static final List<String> CONTENT_TYPES=Arrays.asList("image/jpeg","image/gif","image/png","application/x-jpg");

    @Autowired
    private FastFileStorageClient storageClient;

    @Override
    public String uploadImage(MultipartFile file) {
        //原始文件名
        String originalFilename = file.getOriginalFilename();
        //获取文件类型
        String contentType = file.getContentType();
        //校验文件类型
        if(!CONTENT_TYPES.contains(contentType))
        {
            logger.info("文件类型不合法：{}-{}",contentType,originalFilename);
            return null;
        }
        //校验文件内容
        try {
            BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
            if(bufferedImage==null){
                logger.info("文件内容不合法：{}",originalFilename);
                return null;
            }
            //保存到服务器
            //file.transferTo(new File("E:\\image\\"+originalFilename));
            //返回url进行回显
            //return "http://image.leyou.com/"+originalFilename;

            //获取扩展名
            String ext = StringUtils.substringAfterLast(originalFilename, ".");
            //图片文件上传到 FastDFS
            StorePath storePath = storageClient.uploadFile(file.getInputStream(), file.getSize(), ext, null);

            return "http://image.leyou.com/"+storePath.getFullPath();
        } catch (IOException e) {
            logger.info("服务器内部错误:"+originalFilename);
            e.printStackTrace();
        }
        return null;
    }
}
