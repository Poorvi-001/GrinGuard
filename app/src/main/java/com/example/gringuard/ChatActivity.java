package com.example.gringuard;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import io.noties.markwon.Markwon;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.ChatFutures;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.ai.client.generativeai.type.GenerationConfig;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

public class ChatActivity extends AppCompatActivity {

    private GenerativeModelFutures model;
    private ChatFutures chatSession;
    private androidx.appcompat.widget.AppCompatImageButton sendButton;
    private LinearLayout chatContainer;
    private EditText inputEditText;
    private ScrollView scrollView;
    private Markwon markwon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        markwon       = Markwon.create(this);
        scrollView    = findViewById(R.id.scrollView);
        chatContainer = findViewById(R.id.chatContainer);
        inputEditText = findViewById(R.id.inputEditText);
        sendButton = findViewById(R.id.sendButton);

        GenerationConfig config = new GenerationConfig.Builder().build();
        GenerativeModel gm = new GenerativeModel(
                "gemini-2.5-flash",
                "AIzaSyBGQK_NwUCNqKZhuqzZWWIVbyBY0DTBlT4",
                config
        );
        model = GenerativeModelFutures.from(gm);
        chatSession = model.startChat();

        addBotBubble("👋 Hi! I'm GrinGuard, your dental assistant. How can I help you today?");

        sendButton.setOnClickListener(v -> {
            String text = inputEditText.getText().toString().trim();
            if (!text.isEmpty()) {
                addUserBubble(text);
                askGemini(text);
                inputEditText.setText("");
            }
        });
    }

    private void addUserBubble(String message) {
        runOnUiThread(() -> {
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setGravity(Gravity.END);
            LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            rowParams.setMargins(80, 8, 16, 8);
            row.setLayoutParams(rowParams);

            CardView card = new CardView(this);
            CardView.LayoutParams cardParams = new CardView.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            card.setLayoutParams(cardParams);
            card.setRadius(36f);
            card.setCardElevation(4f);
            card.setCardBackgroundColor(0xFFF48FB1); // light pink

            TextView tv = new TextView(this);
            tv.setText(message);
            tv.setTextColor(0xFFFFFFFF);
            tv.setTextSize(15f);
            tv.setPadding(32, 20, 32, 20);

            card.addView(tv);
            row.addView(card);
            chatContainer.addView(row);
            scrollToBottom();
        });
    }


    private void addBotBubble(String message) {
        runOnUiThread(() -> {
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setGravity(Gravity.START);
            LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            rowParams.setMargins(16, 8, 80, 8);
            row.setLayoutParams(rowParams);

            TextView avatar = new TextView(this);
            avatar.setText("🦷");
            avatar.setTextSize(20f);
            avatar.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams avatarParams = new LinearLayout.LayoutParams(72, 72);
            avatarParams.setMargins(0, 4, 8, 0);
            avatar.setLayoutParams(avatarParams);

            CardView card = new CardView(this);
            CardView.LayoutParams cardParams = new CardView.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            card.setLayoutParams(cardParams);
            card.setRadius(36f);
            card.setCardElevation(4f);
            card.setCardBackgroundColor(0xFFFFFFFF); // white

            TextView tv = new TextView(this);
            markwon.setMarkdown(tv, message);
            tv.setTextColor(0xFF333333);
            tv.setTextSize(15f);
            tv.setPadding(32, 20, 32, 20);

            card.addView(tv);
            row.addView(avatar);
            row.addView(card);
            chatContainer.addView(row);
            scrollToBottom();
        });
    }


    private LinearLayout addTypingBubble() {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.START);
        LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        rowParams.setMargins(16, 8, 80, 8);
        row.setLayoutParams(rowParams);

        CardView card = new CardView(this);
        card.setRadius(36f);
        card.setCardElevation(4f);
        card.setCardBackgroundColor(0xFFFCE4EC); // very light pink

        TextView tv = new TextView(this);
        tv.setText("🦷 GrinGuard is typing...");
        tv.setTextColor(0xFFF48FB1);
        tv.setTextSize(14f);
        tv.setTypeface(null, Typeface.ITALIC);
        tv.setPadding(32, 20, 32, 20);

        card.addView(tv);
        row.addView(card);

        runOnUiThread(() -> {
            chatContainer.addView(row);
            scrollToBottom();
        });

        return row;
    }

    private void scrollToBottom() {
        scrollView.post(() -> scrollView.fullScroll(ScrollView.FOCUS_DOWN));
    }


    private void askGemini(String userText) {
        LinearLayout typingBubble = addTypingBubble();

        String promptWithReminder = userText +
                "\n\n(You are GrinGuard, a dental assistant. " +
                "Reply in maximum 3-4 sentences only. " +
                "Be concise and friendly. " +
                "Only answer dental or oral health questions.)";

        Content content = new Content.Builder().addText(promptWithReminder).build();
        ListenableFuture<GenerateContentResponse> response = chatSession.sendMessage(content);

        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String botReply = result.getText();
                runOnUiThread(() -> {
                    chatContainer.removeView(typingBubble);
                    if (botReply != null) {
                        addBotBubble(botReply);
                    }
                });
            }

            @Override
            public void onFailure(Throwable t) {
                runOnUiThread(() -> {
                    chatContainer.removeView(typingBubble);
                    addBotBubble("Sorry, I couldn't connect. Please check your internet and try again.");
                });
            }
        }, androidx.core.content.ContextCompat.getMainExecutor(this));
    }
}