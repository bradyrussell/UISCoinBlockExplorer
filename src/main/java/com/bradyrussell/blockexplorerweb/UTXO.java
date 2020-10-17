package com.bradyrussell.blockexplorerweb;

public class UTXO {
    public byte[] TransactionHash;
    public int Index;
    public long Amount;

    public UTXO(byte[] transactionHash, int index, long amount) {
        TransactionHash = transactionHash;
        Index = index;
        Amount = amount;
    }
}
