package com.lderic.schoology

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.mamoe.mirai.BotFactory
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.events.FriendMessageEvent
import net.mamoe.mirai.event.events.NewFriendRequestEvent
import net.mamoe.mirai.message.data.content
import net.mamoe.mirai.utils.MiraiLogger
import java.time.Instant
import kotlin.coroutines.EmptyCoroutineContext

object Bot {
    private val bot = BotFactory.newBot(3283056961, "20050415eric")


    val map = mutableMapOf<Long, Schoology>()
    val coroutineScope = CoroutineScope(EmptyCoroutineContext)

    fun init() {
        runBlocking {
            bot.login()
        }
        acceptAllFriend()
        buildinUsers()
        GlobalEventChannel.subscribeAlways<FriendMessageEvent> { event ->
            onMessage(event)
        }
    }

    private fun buildinUsers() {
        map[1278350812] = Schoology("duoli24@js-student.org", "Eric52coco")
        map[2436503132] = Schoology("zilongfang24@js-student.org", "4MarketNotice0!")
    }

    private fun acceptAllFriend() {
        GlobalEventChannel.subscribeAlways<NewFriendRequestEvent> {
            it.accept()
        }
    }

    fun onMessage(event: FriendMessageEvent) {
        coroutineScope.launch {
            val message = event.message.content
            if (message.startsWith("add")) {
                if (map[event.friend.id] != null) {
                    event.friend.sendMessage("您已存在!")
                    return@launch
                }
                val arr = message.split(" ")
                if (arr.size == 3) {
                    map[event.friend.id] = Schoology(arr[1], arr[2])
                    event.friend.sendMessage("添加成功!")
                    return@launch
                }
                event.friend.sendMessage("错误格式! 添加请输入\"add [用户名] [密码]\"")
            } else if (message.startsWith("reset")) {
                val arr = message.split(" ")
                if (arr.size == 1) {
                    map.remove(event.friend.id)
                    event.friend.sendMessage("设置成功!")
                    return@launch
                }
                if (arr.size == 3) {
                    map[event.friend.id] = Schoology(arr[1], arr[2])
                    event.friend.sendMessage("设置成功!")
                    return@launch
                }
                event.friend.sendMessage("错误格式! 重设请输入\"reset [用户名] [密码]\"")
            } else if (message.startsWith("lookup")) {
                val arr = message.split(" ")
                if (arr.size == 2) {
                    if (map[arr[1].toLong()] != null) {
                        event.friend.sendMessage(getAssignmentStr(arr[1].toLong()))
                        return@launch
                    }
                }
                event.friend.sendMessage("用户不存在!")
            } else {
                event.friend.sendMessage(getAssignmentStr(event.friend.id))
            }
        }
    }

    suspend fun getAssignmentStr(id: Long) :String {
        val user = map[id]
        user ?: run {
            return "抱歉(ಥ﹏ಥ) 你的账号并没有关联哟"
        }
        val sb = StringBuilder()
        val now = Instant.now().epochSecond * 1000L
        try {
            var counter = 0
            user.upcoming().forEach {
                if (it.stamp - now <= 86400000 * 2) {
                    counter++
                    sb.append(it.toString()).append("\n")
                }
            }
            sb.insert(0, "叮咚! 在你未来2天内找到了${counter}个作业:\n")
            return sb.toString()
        } catch (_: Exception) {
            return "请检查你的用户名和密码!"
        }
    }
}