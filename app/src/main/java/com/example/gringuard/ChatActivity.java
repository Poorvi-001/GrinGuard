package com.example.gringuard;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.EditText;
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

    private static final String SYSTEM_PROMPT =
            "You are GrinGuard, a dental assistant. " +
                    "Answer ONLY dental or oral health questions. " +
                    "Reply in maximum 3 sentences. " +
                    "Be friendly and concise. " +
                    "If asked anything not related to dental health, " +
                    "politely say you can only help with dental questions.";

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
        sendButton    = findViewById(R.id.sendButton);

        GenerationConfig config = new GenerationConfig.Builder().build();

        // ✅ Using gemini-1.5-flash — stable, free, reliable
        // Replace YOUR_API_KEY_HERE with your key from aistudio.google.com
        GenerativeModel gm = new GenerativeModel(
                "gemini-2.5-flash",
                "AIzaSyAmdZXyaWAKOSYWCAwOBLBfsTQulZc15IQ",
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

    // ─── Send message to Gemini ───────────────────────────────────────────────
    private void askGemini(String userText) {
        LinearLayout typingBubble = addTypingBubble();

        // Remind model to stay on topic and be short with every message
        String fullPrompt = userText + "\n\n(" + SYSTEM_PROMPT + ")";

        Content content = new Content.Builder().addText(fullPrompt).build();

        ListenableFuture<GenerateContentResponse> response =
                chatSession.sendMessage(content);

        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String botReply = result.getText();
                runOnUiThread(() -> {
                    chatContainer.removeView(typingBubble);
                    if (botReply != null && !botReply.isEmpty()) {
                        addBotBubble(botReply);
                    }
                });
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("CHAT_ERROR", "Gemini error: " + t.getMessage(), t);
                runOnUiThread(() -> {
                    chatContainer.removeView(typingBubble);
                    addBotBubble("Sorry, something went wrong. Please try again in a moment.");
                });
            }
        }, androidx.core.content.ContextCompat.getMainExecutor(this));
    }

    // ─── User bubble (pink) ───────────────────────────────────────────────────
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
            card.setCardBackgroundColor(0xFFF48FB1);

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

    // ─── Bot bubble (white) ───────────────────────────────────────────────────
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
            card.setCardBackgroundColor(0xFFFFFFFF);

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

    // ─── Typing indicator ─────────────────────────────────────────────────────
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
        card.setCardBackgroundColor(0xFFFCE4EC);

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
}