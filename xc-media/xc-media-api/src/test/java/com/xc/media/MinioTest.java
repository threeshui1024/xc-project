package com.xc.media;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.UploadObjectArgs;


public class MinioTest {
    static MinioClient minioClient =
             MinioClient.builder()
                     .endpoint("http://localhost:9000")
                     .credentials("minioadmin", "minioadmin")
                     .build();

     //上传文件
    public static void upload() throws Exception {
        boolean found = minioClient.bucketExists(BucketExistsArgs.builder()
                .bucket("testbucket")
                .build());

        //检查testbucket桶是否存在，不存在则自动创建
        if (!found){
            minioClient.makeBucket(MakeBucketArgs.builder().bucket("testbucket").build());
        }else {
            System.out.println("testbucket已经存在");
        }

        //上传
        minioClient.uploadObject(
                UploadObjectArgs.builder().bucket("testbucket").object("1.jpg")
                        .filename("E:\\1.jpg").build());
        System.out.println("上传成功");
    }

    public static void main(String[] args) throws Exception {
        upload();
    }


}
