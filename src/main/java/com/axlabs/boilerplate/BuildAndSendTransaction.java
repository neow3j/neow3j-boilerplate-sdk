package com.axlabs.boilerplate;

import io.neow3j.contract.NeoToken;
import io.neow3j.crypto.WIF;
import io.neow3j.protocol.Neow3j;
import io.neow3j.protocol.core.response.NeoApplicationLog;
import io.neow3j.protocol.core.response.NeoSendRawTransaction;
import io.neow3j.protocol.http.HttpService;
import io.neow3j.transaction.AccountSigner;
import io.neow3j.transaction.Transaction;
import io.neow3j.transaction.TransactionBuilder;
import io.neow3j.types.Hash160;
import io.neow3j.types.Hash256;
import io.neow3j.utils.Await;
import io.neow3j.utils.Numeric;
import io.neow3j.wallet.Account;

import java.math.BigInteger;

public class BuildAndSendTransaction {

    public static void main(String[] args) throws Throwable {

        // Initialize Neow3j to connect to a testnet Neo node.
        Neow3j neow3j = Neow3j.build(new HttpService("http://seed2t4.neo.org:20332"));

        // Initialize Neo token.
        NeoToken neoToken = new NeoToken(neow3j);

        // Define sender and recipient of transfer.
        Hash160 recipient = new Hash160("b897160506030c5d06dc087a21544b4853768012");
        String aliceWif = WIF.getWIFFromPrivateKey(
                Numeric.hexStringToByteArray("6c54536dbd876b92bfc96dd7b9fd6a4286d9a51ac5e26b5cf9becfa27e330918"));
        Account alice = Account.fromWIF(aliceWif);

        // Start building a transfer transaction of NEO.
        TransactionBuilder b = neoToken.transfer(alice, recipient, new BigInteger("10"));

        // Set the signers, sign the transaction and get the signed transaction ready to be sent.
        Transaction tx = b.signers(AccountSigner.calledByEntry(alice))
                .sign();

        // Send the transaction.
        NeoSendRawTransaction response = tx.send();

        // Make sure the node returns no error and then get the transaction hash and wait for execution.
        if (response.hasError()) {
            System.out.printf("Transaction was not successful. Error message from Neo node was: "
                    + "'%s'\n", response.getError().getMessage());
        } else {
            // Get the transaction hash and wait for the transaction to be persisted.
            Hash256 txHash = response.getSendRawTransaction().getHash();
            Await.waitUntilTransactionIsExecuted(txHash, neow3j);

            // Get the transaction's application log and print it.
            NeoApplicationLog applicationLog = neow3j.getApplicationLog(txHash).send().getApplicationLog();
            System.out.println(applicationLog);
        }
    }

}
