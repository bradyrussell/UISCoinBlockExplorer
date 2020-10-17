package com.bradyrussell.blockexplorerweb;

import com.bradyrussell.uiscoin.MagicBytes;
import com.bradyrussell.uiscoin.Util;
import com.bradyrussell.uiscoin.address.UISCoinAddress;
import com.bradyrussell.uiscoin.script.ScriptOperator;

public class UISCoinUtil {
    public static String extractAddressFromScript(byte[] script) {
        if(script.length == 71 && script[0]== ScriptOperator.DUP.OPCode && script[1]==ScriptOperator.SHA512.OPCode && script[2]==ScriptOperator.PUSH.OPCode){
            byte[] pubkeyhash = new byte[64];
            System.arraycopy(script,4,pubkeyhash,0,64);
            return Util.Base64Encode(Util.ConcatArray(Util.ConcatArray(new byte[]{MagicBytes.AddressHeader.Value, MagicBytes.AddressType.Value, MagicBytes.AddressVersion.Value}, pubkeyhash), UISCoinAddress.getChecksumFromPublicKeyHash(pubkeyhash)));
        }
        return null;
    }
}
