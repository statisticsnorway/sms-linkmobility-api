package no.ssb.api.config;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import javax.inject.Singleton;
import java.net.InetSocketAddress;
import java.net.Proxy;

/**
 * Created by runesr on 14.03.2016.
 */
@Configuration
public class SmsApiConfig {

    @Bean
    @Singleton
    public RequestMappingHandlerAdapter requestMappingHandlerAdapter() {
        RequestMappingHandlerAdapter mappingHandlerAdapter = new RequestMappingHandlerAdapter();
        mappingHandlerAdapter.getMessageConverters().add(new MappingJackson2XmlHttpMessageConverter());
        mappingHandlerAdapter.getMessageConverters().add(new Jaxb2RootElementHttpMessageConverter());
        return mappingHandlerAdapter;
    }

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        SimpleClientHttpRequestFactory simpleClientHttpRequestFactory = new SimpleClientHttpRequestFactory();
        InetSocketAddress inetSocketAddress = new InetSocketAddress("proxy.ssb.no",3128);
        Proxy proxy = new Proxy(Proxy.Type.HTTP, inetSocketAddress);
        simpleClientHttpRequestFactory.setProxy(proxy);
        restTemplate.setRequestFactory(simpleClientHttpRequestFactory);
        return restTemplate;
    }

    @Bean
    public ConversionService conversionService() {
        return new DefaultConversionService();
    }
}
