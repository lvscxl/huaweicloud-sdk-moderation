package com.huawei.ais.demo.moderation;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.huawei.ais.demo.ResponseProcessUtils;
import com.huawei.ais.demo.ServiceAccessBuilder;
import com.huawei.ais.sdk.AisAccess;
import com.huawei.ais.sdk.AisAccessWithProxy;
import com.huawei.ais.sdk.util.HttpClientUtils;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;

import java.io.File;
import java.io.IOException;

/**
 *  文本内容检测服务的使用示例类
 */
public class ModerationTextContentDemo {
	//
	// 文本内容检测服务的使用示例函数
	//
	private static void moderationTextContentDemo() throws IOException {

		// 1. 配置好访问文本内容检测服务的基本信息,生成对应的一个客户端连接对象
		AisAccess service = ServiceAccessBuilder.builder()
				.ak("######")                       // your ak
				.sk("######")                       // your sk
				.region("cn-north-4")               // 内容审核服务目前支持华北-北京(cn-north-4)、华东上海一(cn-east-3)、亚太-新加坡(ap-southeast-3)以及亚太-香港(ap-southeast-1)
				.connectionTimeout(5000)            // 连接目标url超时限制
				.connectionRequestTimeout(1000)     // 连接池获取可用连接超时限制
				.socketTimeout(20000)               // 获取服务器响应数据超时限制
				.retryTimes(3)                      // 请求异常时的重试次数
				.build();

		try {
			//
			// 2.构建访问文本内容检测服务需要的参数
			//
			String uri = "/v1.0/moderation/text";

			// api请求参数说明可参考: https://support.huaweicloud.com/api-moderation/moderation_03_0018.html
			JSONObject json = new JSONObject();
			// 注：检测场景支持默认场景和自定义词库场景
			// 自定义词库配置使用可参考:https://support.huaweicloud.com/api-moderation/moderation_03_0027.html
			json.put("categories", new String[] {"porn","politics", "ad", "abuse", "contraband", "flood"});

			JSONObject text = new JSONObject();
			text.put("text", "666666luo聊请+110亚砷酸钾六位qq，fuck666666666666666");
			text.put("type", "content");
			
			JSONArray items = new JSONArray();
			items.add(text);
			
			json.put("items", items);
			
			StringEntity stringEntity = new StringEntity(json.toJSONString(), "utf-8");

			// 3.传入文本内容检测服务对应的uri参数, 传入文本内容检测服务需要的参数，
			// 该参数主要通过JSON对象的方式传入, 使用POST方法调用服务
			HttpResponse response = service.post(uri, stringEntity);

			// 4.验证服务调用返回的状态是否成功，如果为200, 为成功, 否则失败。
			ResponseProcessUtils.processResponseStatus(response);

			// 5.处理服务返回的字符流，输出识别结果。
			JSONObject jsonObject = JSON.parseObject(HttpClientUtils.convertStreamToString(response.getEntity().getContent()));
			System.out.println(JSON.toJSONString(jsonObject, SerializerFeature.PrettyFormat));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			// 6.使用完毕，关闭服务的客户端连接
			service.close();
		}
	}

	//
	// 主入口函数
	//
	public static void main(String[] args) throws IOException {

		// 测试入口函数
		moderationTextContentDemo();
	}
}
