#--
    警报模板
    该模板不局限与使用message.tpl这个名字，可以任意名字，
    模板引擎使用Jfinal Enjoy模板，在模板中，可以使用Enjoy的语法进行编写
--#
告警名称: #(alarmName)
告警模块: #(AlarmUtil.getModelName(appName))
警报级别: #(level)
主机IP: #(AlarmUtil.getHost(host))
告警时间: #(dateTime)
警报内容: #(content)
