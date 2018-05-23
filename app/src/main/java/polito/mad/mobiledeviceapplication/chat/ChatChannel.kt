package polito.mad.mobiledeviceapplication.chat

data class ChatChannel(val userIds: MutableList<String>) {
    constructor() : this(mutableListOf())
}