package com.axlabs.boilerplate;

import io.neow3j.protocol.Neow3j;
import io.neow3j.protocol.http.HttpService;

import java.io.IOException;

public class SubscribeToBlocks {

    public static void main(String[] args) throws IOException {

        // Initialize Neow3j to connect to a testnet Neo node.
        Neow3j neow3j = Neow3j.build(new HttpService("http://seed2t5.neo.org:20332"));

        // Subscribe to new blocks on the testnet.
        neow3j.subscribeToNewBlocksObservable(true)
                .subscribe((blockReqResult) -> {
                    System.out.println("#######################################");
                    System.out.println("Block Index:     " + blockReqResult.getBlock().getIndex());
                    System.out.println("Block Hash:      " + blockReqResult.getBlock().getHash());
                    System.out.println("Prev Block Hash: " + blockReqResult.getBlock().getPrevBlockHash());
                    System.out.println("Next Consensus:  " + blockReqResult.getBlock().getNextConsensus());
                    System.out.println("Transactions:    " + blockReqResult.getBlock().getTransactions());
                });
    }

}
