package com.example.go4lunch.ui.chat;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.example.go4lunch.R;
import com.example.go4lunch.models.Message;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;



public class MessageViewHolder extends RecyclerView.ViewHolder {

    //ROOT VIEW
    RelativeLayout rootView;

    //PROFILE CONTAINER
    LinearLayout profileContainer;
    ImageView imageViewProfile;

    //MESSAGE CONTAINER
    RelativeLayout messageContainer;

    //IMAGE SENDED CONTAINER
   CardView cardViewImageSent;
   ImageView imageViewSent;

    //TEXT MESSAGE CONTAINER
    LinearLayout textMessageContainer;
    TextView textViewMessage;

    //DATE TEXT
    TextView textViewDate;

    //FOR DATA
    private final int colorCurrentUser;
    private final int colorRemoteUser;

    public MessageViewHolder(View itemView) {
        super(itemView);
        colorCurrentUser = ContextCompat.getColor(itemView.getContext(), R.color.colorAccent);
        colorRemoteUser = ContextCompat.getColor(itemView.getContext(), R.color.colorPrimary);
        // Init
        rootView = itemView.findViewById(R.id.activity_chat_item_root_view);
        profileContainer = itemView.findViewById(R.id.activity_chat_item_profile_container);
        imageViewProfile = itemView.findViewById(R.id.activity_chat_item_profile_container_profile_image);
        messageContainer = itemView.findViewById(R.id.activity_chat_item_message_container);
        cardViewImageSent = itemView.findViewById(R.id.activity_chat_item_message_container_image_sent_cardview);
        imageViewSent = itemView.findViewById(R.id.activity_chat_item_message_container_image_sent_cardview_image);
        textMessageContainer = itemView.findViewById(R.id.activity_chat_item_message_container_text_message_container);
        textViewMessage = itemView.findViewById(R.id.activity_chat_item_message_container_text_message_container_text_view);
        textViewDate = itemView.findViewById(R.id.activity_chat_item_message_container_text_view_date);
    }


    public void updateWithMessage(Message message, String currentUserId, RequestManager glide){

        // Check if current user is the sender
        Boolean isCurrentUser = message.getIdUserSender().equals(currentUserId);

        // Update message TextView
        this.textViewMessage.setText(message.getMessage());
        this.textViewMessage.setTextAlignment(isCurrentUser ? View.TEXT_ALIGNMENT_TEXT_END : View.TEXT_ALIGNMENT_TEXT_START);

        // Update date TextView
        if (message.getDateCreated() != null) this.textViewDate.setText(this.convertDateToHour(message.getDateCreated(), itemView.getContext()));

        // Update profile picture ImageView
        if (message.getUrlImageUserSender() != null)
            glide.load(message.getUrlImageUserSender())
                    .apply(RequestOptions.circleCropTransform())
                    .into(imageViewProfile);

        // Update image sent ImageView
        if (message.getUrlImage() != null){
            glide.load(message.getUrlImage())
                    .into(imageViewSent);
            this.imageViewSent.setVisibility(View.VISIBLE);
        } else {
            this.imageViewSent.setVisibility(View.GONE);
        }

        //Update Message Bubble Color Background
        ((GradientDrawable) textMessageContainer.getBackground()).setColor(isCurrentUser ? colorCurrentUser : colorRemoteUser);

        // Update all views alignment depending is current user or not
        this.updateDesignDependingUser(isCurrentUser);
    }

    private void updateDesignDependingUser(Boolean isSender){

        // PROFILE CONTAINER
        RelativeLayout.LayoutParams paramsLayoutHeader = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        paramsLayoutHeader.addRule(isSender ? RelativeLayout.ALIGN_PARENT_RIGHT : RelativeLayout.ALIGN_PARENT_LEFT);
        this.profileContainer.setLayoutParams(paramsLayoutHeader);

        // MESSAGE CONTAINER
        RelativeLayout.LayoutParams paramsLayoutContent = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        paramsLayoutContent.addRule(isSender ? RelativeLayout.LEFT_OF : RelativeLayout.RIGHT_OF, R.id.activity_chat_item_profile_container);
        this.messageContainer.setLayoutParams(paramsLayoutContent);

        // CARDVIEW IMAGE SEND
        RelativeLayout.LayoutParams paramsImageView = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        paramsImageView.addRule(isSender ? RelativeLayout.ALIGN_LEFT : RelativeLayout.ALIGN_RIGHT, R.id.activity_chat_item_message_container_text_message_container);
        this.cardViewImageSent.setLayoutParams(paramsImageView);
    }

    // ---

    private String convertDateToHour(Date date, Context context){
        if (android.text.format.DateFormat.is24HourFormat(context)) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy HH:mm", Locale.getDefault());
            return dateFormat.format(date);
        }else{
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy h.mm a", Locale.getDefault());
            return dateFormat.format(date);
        }
    }
}
