package com.zx.insectdetection.mapper;

import com.zx.insectdetection.entity.detect.DetectImgResult;
import com.zx.insectdetection.entity.detect.DetectImgInfo;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface DetectMapper {
    @Insert("insert into img_url(user_id,img_url,create_time) values(#{uid},#{imgUrl},now())")
    int addImgUrl(@Param("imgUrl")String imgUrl, @Param("uid") Integer uid);

    @Select("select id from img_url order by create_time desc limit 1;")
    Integer selectLastId();

    @Select("select img_url.*, detect_detail.* from img_url left join detect_detail on img_url.id = detect_detail .img_id order by img_url.create_time desc")
    List<DetectImgInfo> getDetectHistory(Integer id);

    @Insert("insert into detect_detail(img_id,sort,confidence_level)" +
            " values(#{imgId},#{sort},#{confidenceLevel})")
    void insertDetectInfo(DetectImgResult detectImgResult);

    @Insert("insert into detect_detail(img_id,sort,confidence_level,user_id)" +
            " values(#{imgId},#{sort},#{confidenceLevel},#{userId})")
    void insertDetectResult(@Param("imgId")Integer imgId, @Param("sort")String sort, @Param("confidenceLevel")double confidenceLevel, @Param("userId")Integer userId);

    @Select("select img_url.*, detect_detail.* from img_url left join detect_detail on img_url.id = detect_detail .img_id where img_url.user_id = #{userId} order by img_url.create_time desc")
    List<DetectImgInfo> getUserDetectList(@Param("userId") Integer userId);

    @Delete("delete from img_url where id = #{imgId}")
    void deleteDetectRecord(Integer imgId);

    @Select("select img_url.*, detect_detail.* from img_url left join detect_detail on img_url.id = detect_detail.img_id order by img_url.create_time desc")
    List<DetectImgInfo> getAllDetectList();
}
