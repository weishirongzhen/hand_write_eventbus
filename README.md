# hand_write_eventbus
手写简易版eventbus，主功能实现， 带线程切换， 4个类共130行代码


原理：
[实现自定义注解]->
[registe(this)的时候遍历this,以及this的父类拥有自定义注解的方法，保存到map]->
[post(obj)时遍历寻找与注解方法时与obj相同类型的method]->
[判断线程]->
[根据线程invoke method]->
[结束]


unregister 不实现，原理， 在 ondestroy中 删除对应通过register(this)添加到map中的this 即可
