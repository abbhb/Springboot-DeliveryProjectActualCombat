package com.qc.ssm.ssmstudy.reggie.controller;

import com.alipay.easysdk.factory.Factory;
import com.alipay.easysdk.payment.page.models.AlipayTradePagePayResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.qc.ssm.ssmstudy.reggie.common.Code;
import com.qc.ssm.ssmstudy.reggie.pojo.entity.AliPay;
import com.qc.ssm.ssmstudy.reggie.pojo.entity.TransactionFlow;
import com.qc.ssm.ssmstudy.reggie.service.OrdersService;
import com.qc.ssm.ssmstudy.reggie.service.TransactionFlowService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController//@ResponseBody+@Controller
@RequestMapping("/alipay")
@Slf4j
public class AliPayController {
    private final OrdersService ordersService;

    private final TransactionFlowService transactionFlowService;

    @Autowired
    public AliPayController(OrdersService ordersService, TransactionFlowService transactionFlowService) {
        this.ordersService = ordersService;
        this.transactionFlowService = transactionFlowService;
    }

    @GetMapping("/pay") // &subject=xxx&traceNo=xxx&totalAmount=xxx
    public String pay(AliPay aliPay) {
        AlipayTradePagePayResponse response;
        try {
            //  发起API调用（以创建当面付收款二维码为例）
            response = Factory.Payment.Page()
                    .pay(URLEncoder.encode(aliPay.getSubject(), StandardCharsets.UTF_8), aliPay.getTraceNo(), aliPay.getTotalAmount(), "");
        } catch (Exception e) {
            System.err.println("调用遭遇异常，原因：" + e.getMessage());
            throw new RuntimeException(e.getMessage(), e);
        }
        return response.getBody();
    }

    /**
     * 支付宝回调
     * 如何保证这个接口的安全性,只能让支付宝调用
     *
     *
     * 手动事物
     * return success后支付宝不会重复发success通知，如果返回fail会隔一段时间再发
     * 当收到退款回调时该怎处理
     * 怎么在找不到订单的时候自动退款
     * 支付宝pay接口有个公共回传参数
     * @param request
     * @return
     * @throws Exception
     */
    @PostMapping("/notify")  // 注意这里必须是POST接口,此接口是给支付宝调的
    @Transactional
    public String payNotify(HttpServletRequest request) throws Exception{

        if (request.getParameter("trade_status").equals("TRADE_SUCCESS")||request.getParameter("trade_status").equals("TRADE_FINISHED")) {
            System.out.println("=========支付宝异步回调========");

            Map<String, String> params = new HashMap<>();
            Map<String, String[]> requestParams = request.getParameterMap();
            for (String name : requestParams.keySet()) {
                params.put(name, request.getParameter(name));
                // System.out.println(name + " = " + request.getParameter(name));
            }

            String tradeNo = params.get("out_trade_no");
            String gmtPayment = params.get("gmt_payment");
            String alipayTradeNo = params.get("trade_no");
            // 支付宝验签
            if (Factory.Payment.Common().verifyNotify(params)) {
                // 验签通过
                //写入数据库
                SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date myDate1 = null;
                LocalDateTime localDateTime = null;
                try {
                    myDate1 = sdf1.parse(params.get("gmt_payment"));
                    Instant instant = myDate1.toInstant();
                    ZoneId zoneId = ZoneId.systemDefault();
                    localDateTime = instant.atZone(zoneId).toLocalDateTime();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                LambdaQueryWrapper<TransactionFlow> lambdaQueryWrapper = new LambdaQueryWrapper<>();
                lambdaQueryWrapper.eq(TransactionFlow::getTradeNo,Long.valueOf(alipayTradeNo));
                TransactionFlow one = transactionFlowService.getOne(lambdaQueryWrapper);
                if (one!=null){
                    return "success";
                }

                //支付成功，finish暂时不管他，反正现在是到账就返回success
                // 更新订单-->已支付
                boolean b = ordersService.updateState(tradeNo, Code.OrdersStatusToBeDelivered, localDateTime, Code.OrdersPayWayZFB);//待派送就是已付款
                if (!b) {
                    //问题单，钱到了，状态没改
                    //此处得想办法掉退款
                    //目前在前端页面告知用户，让用户拿截图找客服
                    log.error("需要退款,流水号为{}", params.get("trade_no"));
                }

                //不管成功还是失败，得在库里留存
                TransactionFlow transactionFlow = new TransactionFlow();
                transactionFlow.setBuyerPayAmount(params.get("buyer_pay_amount"));
                transactionFlow.setSubject(params.get("subject"));
                transactionFlow.setAmount(params.get("total_amount"));
                transactionFlow.setBuyerId(Long.valueOf(params.get("buyer_id")));
                transactionFlow.setGmtPayment(localDateTime);
                transactionFlow.setTradeNo(Long.valueOf(alipayTradeNo));//流水号
                transactionFlow.setOutTradeNo(Long.valueOf(params.get("out_trade_no")));
                transactionFlow.setTradeStatus(params.get("trade_status"));
                transactionFlowService.save(transactionFlow);
            }
        }
        return "success";

    }
}
