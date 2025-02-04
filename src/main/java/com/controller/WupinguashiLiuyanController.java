










package com.controller;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import com.alibaba.fastjson.JSONObject;
import java.util.*;
import org.springframework.beans.BeanUtils;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.context.ContextLoader;
import javax.servlet.ServletContext;
import com.service.TokenService;
import com.utils.StringUtil;
import java.lang.reflect.InvocationTargetException;

import com.service.DictionaryService;
import org.apache.commons.lang3.StringUtils;
import com.annotation.IgnoreAuth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.entity.*;
import com.entity.view.*;
import com.service.*;
import com.utils.PageUtils;
import com.utils.R;
import com.alibaba.fastjson.*;

/**
 * 物品挂失留言
 * 后端接口
 * @author
 * @email
*/
@RestController
@Controller
@RequestMapping("/wupinguashiLiuyan")
public class WupinguashiLiuyanController {
    private static final Logger logger = LoggerFactory.getLogger(WupinguashiLiuyanController.class);

    @Autowired
    private WupinguashiLiuyanService wupinguashiLiuyanService;


    @Autowired
    private TokenService tokenService;
    @Autowired
    private DictionaryService dictionaryService;

    //级联表service
    @Autowired
    private WupinguashiService wupinguashiService;
    @Autowired
    private YonghuService yonghuService;



    /**
    * 后端列表
    */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params, HttpServletRequest request){
        logger.debug("page方法:,,Controller:{},,params:{}",this.getClass().getName(),JSONObject.toJSONString(params));
        String role = String.valueOf(request.getSession().getAttribute("role"));
        if(StringUtil.isEmpty(role))
            return R.error(511,"权限为空");
        else if("用户".equals(role))
            params.put("yonghuId",request.getSession().getAttribute("userId"));
        if(params.get("orderBy")==null || params.get("orderBy")==""){
            params.put("orderBy","id");
        }
        PageUtils page = wupinguashiLiuyanService.queryPage(params);

        //字典表数据转换
        List<WupinguashiLiuyanView> list =(List<WupinguashiLiuyanView>)page.getList();
        for(WupinguashiLiuyanView c:list){
            //修改对应字典表字段
            dictionaryService.dictionaryConvert(c, request);
        }
        return R.ok().put("data", page);
    }

    /**
    * 后端详情
    */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id, HttpServletRequest request){
        logger.debug("info方法:,,Controller:{},,id:{}",this.getClass().getName(),id);
        WupinguashiLiuyanEntity wupinguashiLiuyan = wupinguashiLiuyanService.selectById(id);
        if(wupinguashiLiuyan !=null){
            //entity转view
            WupinguashiLiuyanView view = new WupinguashiLiuyanView();
            BeanUtils.copyProperties( wupinguashiLiuyan , view );//把实体数据重构到view中

                //级联表
                WupinguashiEntity wupinguashi = wupinguashiService.selectById(wupinguashiLiuyan.getWupinguashiId());
                if(wupinguashi != null){
                    BeanUtils.copyProperties( wupinguashi , view ,new String[]{ "id", "createDate"});//把级联的数据添加到view中,并排除id和创建时间字段
                    view.setWupinguashiId(wupinguashi.getId());
                }
                //级联表
                YonghuEntity yonghu = yonghuService.selectById(wupinguashiLiuyan.getYonghuId());
                if(yonghu != null){
                    BeanUtils.copyProperties( yonghu , view ,new String[]{ "id", "createDate"});//把级联的数据添加到view中,并排除id和创建时间字段
                    view.setYonghuId(yonghu.getId());
                }
            //修改对应字典表字段
            dictionaryService.dictionaryConvert(view, request);
            return R.ok().put("data", view);
        }else {
            return R.error(511,"查不到数据");
        }

    }

    /**
    * 后端保存
    */
    @RequestMapping("/save")
    public R save(@RequestBody WupinguashiLiuyanEntity wupinguashiLiuyan, HttpServletRequest request){
        logger.debug("save方法:,,Controller:{},,wupinguashiLiuyan:{}",this.getClass().getName(),wupinguashiLiuyan.toString());

        String role = String.valueOf(request.getSession().getAttribute("role"));
        if(StringUtil.isEmpty(role))
            return R.error(511,"权限为空");
        else if("用户".equals(role))
            wupinguashiLiuyan.setYonghuId(Integer.valueOf(String.valueOf(request.getSession().getAttribute("userId"))));
        wupinguashiLiuyan.setInsertTime(new Date());
        wupinguashiLiuyan.setCreateTime(new Date());
        wupinguashiLiuyanService.insert(wupinguashiLiuyan);
        return R.ok();
    }

    /**
    * 后端修改
    */
    @RequestMapping("/update")
    public R update(@RequestBody WupinguashiLiuyanEntity wupinguashiLiuyan, HttpServletRequest request){
        logger.debug("update方法:,,Controller:{},,wupinguashiLiuyan:{}",this.getClass().getName(),wupinguashiLiuyan.toString());

        String role = String.valueOf(request.getSession().getAttribute("role"));
        if(StringUtil.isEmpty(role))
            return R.error(511,"权限为空");
        else if("用户".equals(role))
            wupinguashiLiuyan.setYonghuId(Integer.valueOf(String.valueOf(request.getSession().getAttribute("userId"))));
        //根据字段查询是否有相同数据
        Wrapper<WupinguashiLiuyanEntity> queryWrapper = new EntityWrapper<WupinguashiLiuyanEntity>()
            .eq("id",0)
            ;

        logger.info("sql语句:"+queryWrapper.getSqlSegment());
        WupinguashiLiuyanEntity wupinguashiLiuyanEntity = wupinguashiLiuyanService.selectOne(queryWrapper);
        wupinguashiLiuyan.setUpdateTime(new Date());
        if(wupinguashiLiuyanEntity==null){
            //  String role = String.valueOf(request.getSession().getAttribute("role"));
            //  if("".equals(role)){
            //      wupinguashiLiuyan.set
            //  }
            wupinguashiLiuyanService.updateById(wupinguashiLiuyan);//根据id更新
            return R.ok();
        }else {
            return R.error(511,"表中有相同数据");
        }
    }

    /**
    * 删除
    */
    @RequestMapping("/delete")
    public R delete(@RequestBody Integer[] ids){
        logger.debug("delete:,,Controller:{},,ids:{}",this.getClass().getName(),ids.toString());
        wupinguashiLiuyanService.deleteBatchIds(Arrays.asList(ids));
        return R.ok();
    }




    /**
    * 前端列表
    */
    @IgnoreAuth
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params, HttpServletRequest request){
        logger.debug("list方法:,,Controller:{},,params:{}",this.getClass().getName(),JSONObject.toJSONString(params));

        // 没有指定排序字段就默认id倒序
        if(StringUtil.isEmpty(String.valueOf(params.get("orderBy")))){
            params.put("orderBy","id");
        }
        PageUtils page = wupinguashiLiuyanService.queryPage(params);

        //字典表数据转换
        List<WupinguashiLiuyanView> list =(List<WupinguashiLiuyanView>)page.getList();
        for(WupinguashiLiuyanView c:list)
            dictionaryService.dictionaryConvert(c, request); //修改对应字典表字段
        return R.ok().put("data", page);
    }

    /**
    * 前端详情
    */
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id, HttpServletRequest request){
        logger.debug("detail方法:,,Controller:{},,id:{}",this.getClass().getName(),id);
        WupinguashiLiuyanEntity wupinguashiLiuyan = wupinguashiLiuyanService.selectById(id);
            if(wupinguashiLiuyan !=null){
                //entity转view
                WupinguashiLiuyanView view = new WupinguashiLiuyanView();
                BeanUtils.copyProperties( wupinguashiLiuyan , view );//把实体数据重构到view中

                //级联表
                    WupinguashiEntity wupinguashi = wupinguashiService.selectById(wupinguashiLiuyan.getWupinguashiId());
                if(wupinguashi != null){
                    BeanUtils.copyProperties( wupinguashi , view ,new String[]{ "id", "createDate"});//把级联的数据添加到view中,并排除id和创建时间字段
                    view.setWupinguashiId(wupinguashi.getId());
                }
                //级联表
                    YonghuEntity yonghu = yonghuService.selectById(wupinguashiLiuyan.getYonghuId());
                if(yonghu != null){
                    BeanUtils.copyProperties( yonghu , view ,new String[]{ "id", "createDate"});//把级联的数据添加到view中,并排除id和创建时间字段
                    view.setYonghuId(yonghu.getId());
                }
                //修改对应字典表字段
                dictionaryService.dictionaryConvert(view, request);
                return R.ok().put("data", view);
            }else {
                return R.error(511,"查不到数据");
            }
    }


    /**
    * 前端保存
    */
    @RequestMapping("/add")
    public R add(@RequestBody WupinguashiLiuyanEntity wupinguashiLiuyan, HttpServletRequest request){
        logger.debug("add方法:,,Controller:{},,wupinguashiLiuyan:{}",this.getClass().getName(),wupinguashiLiuyan.toString());
        wupinguashiLiuyan.setInsertTime(new Date());
        wupinguashiLiuyan.setCreateTime(new Date());
        wupinguashiLiuyanService.insert(wupinguashiLiuyan);
        return R.ok();
        }



}
