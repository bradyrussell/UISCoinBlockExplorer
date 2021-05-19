package com.bradyrussell.blockexplorerweb;

import com.bradyrussell.uiscoin.BytesUtil;
import com.bradyrussell.uiscoin.MagicBytes;
import com.bradyrussell.uiscoin.address.UISCoinAddress;
import com.bradyrussell.uiscoin.script.ScriptMatcher;
import com.bradyrussell.uiscoin.script.ScriptOperator;

public class UISCoinUtil {
    public static String extractAddressFromScript(byte[] script) {
        ScriptMatcher matcherP2PK = ScriptMatcher.getMatcherP2PK();
        if(matcherP2PK.match(script)) {
            return "Pay to Public Key: "+BytesUtil.Base64Encode(matcherP2PK.getPushData(0)) + (matcherP2PK.getPushCount() > 1 ? " Memo: "+new String(matcherP2PK.getPushData(1)) : "");
        }
        ScriptMatcher matcherP2PKH = ScriptMatcher.getMatcherP2PKH();
        if(matcherP2PKH.match(script)) {
            return "Pay to Public Key Hash: "+BytesUtil.Base64Encode(matcherP2PKH.getPushData(0)) + (matcherP2PKH.getPushCount() > 1 ? " Memo: "+new String(matcherP2PKH.getPushData(1)) : "");
        }
        ScriptMatcher matcherP2Password = ScriptMatcher.getMatcherP2Password();
        if(matcherP2Password.match(script)) {
            return "Pay to Password: "+BytesUtil.Base64Encode(matcherP2Password.getPushData(0)) + (matcherP2Password.getPushCount() > 1 ? " Memo: "+new String(matcherP2Password.getPushData(1)) : "");
        }
        ScriptMatcher matcherP2SH = ScriptMatcher.getMatcherP2SH();
        if(matcherP2SH.match(script)) {
            return "Pay to Script Hash: "+BytesUtil.Base64Encode(matcherP2SH.getPushData(0)) + (matcherP2SH.getPushCount() > 1 ? " Memo: "+new String(matcherP2SH.getPushData(1)) : "");
        }

        return null;
    }
}
