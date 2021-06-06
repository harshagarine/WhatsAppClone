package com.garine.whatsappclone.interfaces;

import com.garine.whatsappclone.model.chat.Chats;

import java.util.List;

public interface OnReadChatCallBack {
    void onReadSuccess(List<Chats> list);
    void onReadFailed();
}
