package com.neighbor.neighborsrefrigerator.viewmodels

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neighbor.neighborsrefrigerator.data.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import com.neighbor.neighborsrefrigerator.utilities.App
import com.neighbor.neighborsrefrigerator.utilities.MyTypeConverters


class ChatListViewModel: ViewModel() {
    var example: ChatListData = ChatListData(
        Chat(
        postId = "1234",
        writer = Inform(id = "1", nickname = "seoyeon", 1),
        contact = Inform(id = "2", nickname = "zinkiki", 2),
        message = listOf(ChatMessage(content = "안녕하세요", false, "2022-08-26 17:47:01", 2))
        ),
        12345678
    )
    var chatData = MutableStateFlow<List<ChatListData>?>(listOf(example))
    var nickname = MutableStateFlow<String>("")
    var lastMessage = MutableStateFlow<String>("")
    var newMessage = MutableStateFlow<Int>(0)
    var createAt = MutableStateFlow<Long>(0)
    private val chatListHashMap = MutableStateFlow<HashMap<ChatListData, Long>?>(null)



    // 싱글톤 패턴을 사용하지 않을 경우
/*    private val db = Room.databaseBuilder(
        App.context(),
        ChatListDB::class.java,
        "chatList-database"
    ).build()*/

    // 싱글톤 패턴을 사용할 경우
    private var db = ChatListDB.getInstance(App.context())


    fun initChatList(){
        viewModelScope.launch {

            val chats = CoroutineScope(Dispatchers.IO).async{
                db?.chatListDao()!!.getChatMessage()
            }.await()

            chatData.value = chats

            chatData.value!!.forEach{ chatListData ->
                //chatListHashMap.value?.set(chatListData, getLastChatTimestamp())
                chatListData.chatData?.message?.forEach {
                    it.created_at = MyTypeConverters().convertDateToTimeStamp(it.created_at!!).toString()
                }
            }

        }
        // chatList 정렬 - 각 chat들의 가장 최근 메시지 중 더 큰 값 순으로 정렬


        // chatData timestamp 순으로 배치
/*        chatData.value?.forEach {

            refreshChatList(it)

        }*/
    }

    private fun getLastChatTimestamp(chat: Chat): Long?{

        // 마지막 메세지 기준 - 더 최근일수록 숫자가 커짐
        val lastChat = chat.message.maxWithOrNull(compareBy { it.created_at?.let { date ->
            MyTypeConverters().convertDateToTimeStamp(date)
        } })

        return MyTypeConverters().convertDateToTimeStamp(lastChat?.created_at.toString())
    }
    fun refreshChatList(chat: Chat){
        // 채팅 하나마다 안읽은 메세지 수, 마지막 채팅, 상대방 닉네임 및 정보 가져와야함

        // 최근 채팅 순으로 정렬
        // chatListDetail에 리스트 정보 저장 방법
        // 가장 최근 채팅 먼저 올리기
        checkNewMessage(chat)   //  새 메세지 개수 가져오기
        checkLastChat(chat)
        // 상대방 정보 -> contactId 체크해서 본인 아니면 postId로 postData 가져와서 작성자 정보 가져와야함
        getUserData(chat)   //  상대 닉네임 가져오기
    }

    private fun sortChatList(){


    }

    private fun checkNewMessage(chat: Chat){
        newMessage.value = 0
        chat.message.forEach {
            if(!it.isRead){
                newMessage.value++
            }
        }
    }

    private fun checkLastChat(chat: Chat) {
        // 마지막 메세지 기준 - 더 최근일수록 숫자가 커짐

        val lastChat = chat.message.maxWithOrNull(compareBy { it.created_at?.let { date ->
            MyTypeConverters().convertDateToTimeStamp(date)
        } })
        val current = System.currentTimeMillis()
        val lastChatTime = MyTypeConverters().convertDateToTimeStamp(lastChat?.created_at.toString())!!

/*        compareBy<ChatMessage>{ it.created_at?.let { it ->
            MyTypeConverters().convertDateToTimeStamp(
                it
            )
        } }*/

        lastMessage.value = MyTypeConverters().convertTimestampToStringDate(current, lastChatTime).toString()
    }

    private fun getUserData(chat: Chat){
        // 상대방 정보 -> contactId 체크해서 본인 아니면 postId로 postData 가져와서 작성자 정보 가져와야함
        if(chat.writer.id == UserSharedPreference(App.context()).getUserPrefs("id")){
            nickname.value = chat.contact.id.toString()
        }
        else if(chat.contact.id == UserSharedPreference(App.context()).getUserPrefs("id")){
            nickname.value = chat.writer.id.toString()
        }
        // 아닐 경우 writerId 체크해서 상대방 정보 가져오기
    }
}