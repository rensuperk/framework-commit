# framework-commi
方法这个是控制是否重复提交的模块


依赖公司的用户模块


使用注解，aop来控制
实现的很简单
有个缺陷应该用concurrentHashMap来实现,不应该用hashMap实现,有线程同步的问题
