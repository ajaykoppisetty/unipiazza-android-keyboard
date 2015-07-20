package com.example.unipiazzakeyboard;

import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.inputmethodservice.KeyboardView.OnKeyboardActionListener;
import android.media.AudioManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

public class UnipiazzaKeyboard extends InputMethodService
        implements OnKeyboardActionListener {

    private KeyboardView kv;
    private Keyboard mCurrentKeyboard;
    private boolean caps = false;
    private Keyboard emailKeyboard;
    private Keyboard textKeyboard;

    private void playClick(int keyCode) {
        AudioManager am = (AudioManager) getSystemService(AUDIO_SERVICE);
        switch (keyCode) {
            case 32:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_SPACEBAR);
                break;
            case Keyboard.KEYCODE_DONE:
            case 10:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_RETURN);
                break;
            case Keyboard.KEYCODE_DELETE:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_DELETE);
                break;
            default:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD);
        }
    }

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        InputConnection ic = getCurrentInputConnection();
        playClick(primaryCode);
        switch (primaryCode) {
            case Keyboard.KEYCODE_DELETE:
                ic.deleteSurroundingText(1, 0);
                break;
            case Keyboard.KEYCODE_SHIFT:
                caps = !caps;
                mCurrentKeyboard.setShifted(caps);
                kv.invalidateAllKeys();
                break;
            case Keyboard.KEYCODE_DONE:
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
                break;
            default:
                char code = (char) primaryCode;
                if (Character.isLetter(code) && caps) {
                    code = Character.toUpperCase(code);
                }
                ic.commitText(String.valueOf(code), 1);
        }
    }

    @Override
    public View onCreateInputView() {
        kv = (KeyboardView) getLayoutInflater().inflate(R.layout.keyboard, null);
        Log.v("UNIPIAZZA", "onCreateInputView, mCurrentKeyboard=" + mCurrentKeyboard);
        mCurrentKeyboard = textKeyboard;
        kv.setKeyboard(mCurrentKeyboard);
        kv.setOnKeyboardActionListener(this);
        kv.setPreviewEnabled(false);
        return kv;
    }

    @Override
    public void onPress(int primaryCode) {
    }

    @Override
    public void onRelease(int primaryCode) {
    }

    @Override
    public void onText(CharSequence text) {
        InputConnection ic = getCurrentInputConnection();
        CharSequence code = text;
        ic.commitText(String.valueOf(code), 1);
    }

    @Override
    public void swipeDown() {
    }

    @Override
    public void swipeLeft() {
    }

    @Override
    public void swipeRight() {
    }

    @Override
    public void swipeUp() {
    }

    @Override
    public void onInitializeInterface() {
        super.onInitializeInterface();
        emailKeyboard = new Keyboard(this, R.xml.qwertyemail);
        textKeyboard = new Keyboard(this, R.xml.qwerty);
    }

    @Override
    public void onStartInputView(EditorInfo info, boolean restarting) {
        super.onStartInputView(info, restarting);
        mCurrentKeyboard = textKeyboard;

        switch (info.inputType & EditorInfo.TYPE_MASK_VARIATION) {
            case EditorInfo.TYPE_TEXT_VARIATION_EMAIL_ADDRESS:
                mCurrentKeyboard = emailKeyboard;
                Log.v("UNIPIAZZA", "emailKeyboard, mCurrentKeyboard=" + mCurrentKeyboard);
        }

        Log.v("UNIPIAZZA", "mCurrentKeyboard=" + mCurrentKeyboard);
        kv.setKeyboard(mCurrentKeyboard);
    }
}