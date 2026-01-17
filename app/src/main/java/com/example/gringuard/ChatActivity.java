package com.example.gringuard;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
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
    private TextView chatResponse;
    private EditText inputEditText;
    private Button sendButton;
    private ScrollView scrollView;
    private Markwon markwon;

    //    private io.noties.markwon.Markwon markwon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        markwon = io.noties.markwon.Markwon.create(this);
        chatResponse = findViewById(R.id.chatResponse);
        inputEditText = findViewById(R.id.inputEditText);
        sendButton = findViewById(R.id.sendButton);
        scrollView = findViewById(R.id.scrollView); // Ensure your XML has this ID

        GenerationConfig config = new GenerationConfig.Builder().build();

        GenerativeModel gm = new GenerativeModel(
                "gemini-2.5-flash",
                "AIzaSyC-WLJ8grez4nq12F91DkFczjWT6EwICEM",
                config
        );
        model = GenerativeModelFutures.from(gm);
        chatSession = model.startChat();

        // Initial Greeting
        chatResponse.setText("");
        appendChatLog("GrinGuard", "Welcome! I am your dental assistant. How can I help you today?");

        sendButton.setOnClickListener(v -> {
            String text = inputEditText.getText().toString().trim();
            if (!text.isEmpty()) {
                appendChatLog("You", text);
                askGemini(text);
                inputEditText.setText("");
            }
        });
    }

    private void appendChatLog(String sender, String message) {
        runOnUiThread(() -> {
            SpannableStringBuilder builder = new SpannableStringBuilder();

            // 1. If there is already text, add space before the new message block
            if (chatResponse.getText().length() > 0) {
                builder.append("\n\n");
            }

            int start = builder.length();
            // 2. Add Name + Newline so text starts below the name
            builder.append(sender).append(":\n");

            // 3. Style: Always BOLD
            builder.setSpan(new StyleSpan(Typeface.BOLD), start, builder.length(), 0);

            // 4. Style: Colors (Back to your original settings)
            // You = Pink, GrinGuard = Black
            int color = sender.equals("You") ? 0xFFFF1493 : 0xFF000000;
            builder.setSpan(new ForegroundColorSpan(color), start, builder.length(), 0);

            chatResponse.append(builder);

            // 5. Append message content
            if (sender.equals("GrinGuard")) {
                // Get all text currently in the box, add the new bot reply, and re-render
                String combinedText = chatResponse.getText().toString() + message;
                markwon.setMarkdown(chatResponse, combinedText);
            } else {
                // For the user, just simple append
                chatResponse.append(message);
            }

            // 6. Scroll to bottom
            scrollView.post(() -> scrollView.fullScroll(ScrollView.FOCUS_DOWN));
        });
    }

    private void askGemini(String userText) {
        Content content = new Content.Builder().addText(userText).build();
        ListenableFuture<GenerateContentResponse> response = chatSession.sendMessage(content);

        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String botReply = result.getText();
                runOnUiThread(() -> {
                    if (botReply != null) {
                        appendChatLog("GrinGuard", botReply);
                    }
                });
            }

            @Override
            public void onFailure(Throwable t) {
                runOnUiThread(() -> {
                    appendChatLog("System", "Error: Check connection.");
                });
            }
        }, androidx.core.content.ContextCompat.getMainExecutor(this));
    }
}