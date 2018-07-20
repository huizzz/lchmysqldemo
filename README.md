# lchmysqldemo
下载源码 可将其通过maven的install方法打包到本地maven仓库中然后通过添加依赖

<dependency>
            <groupId>com.lch</groupId>
            <artifactId>lch-mybatis-demo</artifactId>
            <version>2.7-SNAPSHOT</version>
        </dependency>
或者直接将源码拖到自己的项目中即可使用

第一现在配置文件中配置

数据库的链接地址 只需要填写成 xx.xxx.xx.xxx:3306 格式的就可以了

package-scanner是需要扫描的包路径 不填写的话 默认"/" 全局扫描

username和password是数据库对应的账号密码 不填写的话默认root和""

lch.mybatis.url=
lch.mybatis.username=
lch.mybatis.password=
lch.mybatis.package-scanner=
在boot的启动类上加注解@EnableStartLch 即可

@EnableStartLch
@SpringBootApplication
public class DemotestApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemotestApplication.class, args);
    }
}
定义DAO层接口 需要在接口上注解上你的数据库的库名 即可直接使用

@LchRepository(db = "DBName")
public interface testDAO {

    @LchQuery("select * from chat_userinfo where user_id = 1072")
    List<entity> find();

    @LchQuery("select * from chat_userinfo where user_id = 1072")
    entity find2();

    @LchQuery("select * from chat_userinfo where user_id = ?")
    entity find3(String id);

    @LchQuery("select * from chat_userinfo where user_id = ?")
    List<entity> find4(String id);

    @LchUpdate("update chat_userinfo set user_call_time = 3789 where user_id = 1072")
    int update();

    @LchUpdate("update chat_userinfo set user_call_time = ? where user_id = 1072")
    Integer update2(String id);

    @LchUpdate("update chat_userinfo set user_call_time = 3789 where user_id = ?")
    int update3(String id);

    @LchUpdate("update chat_userinfo set user_call_time = ? where user_id = 1072")
    Integer update4(String id);
}
demo提供了增删改查4个注解用于区分不同的sql语句

需要注意的是增删改的返回值必须是integer或者int 否则会报转型失败的异常

而查询会根据你的返回值是是否是list而进行封装 如果返回值非list 而值大于1条的话 则只返回第一条

传入的参数会按照sql语句中?的顺序依次插入 不能多也不能少 否则也会报异常

至于为什么这么写的原因是按照jdbc的方式描写

编写测试:

@RunWith(SpringRunner.class)
@SpringBootTest
public class DemotestApplicationTests {

    @Autowired
    private testDAO dao;

    @Test
    public void contextLoads() throws Exception {

         var a = dao.find();

         a.forEach(e-> System.out.println(e.toString()));

         var b = dao.find2();

         System.out.println(b.toString());

         var c = dao.find3("1072");

         System.out.println(c.toString());

         var d = dao.find4("1072");
         d.forEach(System.out::println);

         var e = dao.update();

        System.out.println(e);

         var f = dao.update2("33");

        System.out.println(f);

         var g = dao.update3("1072");

        System.out.println(g);

        var h = dao.update4("1072");

        System.out.println(h);
        
    }

}


经本人测试的结果是 上面的测试案例都是可以 使用起来和注解式的mybaits一样

接下来我将分享一下如何实现的

huiz：如何写一个mybaits注解式的springboot框架
​zhuanlan.zhihu.com
图标
demo只花了一天写加测试 非常简陋 代码量很少 如果谁有兴趣的话可以和我一起改进一下
