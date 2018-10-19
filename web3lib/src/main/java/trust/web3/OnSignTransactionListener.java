package trust.web3;

import trust.core.entity.Transaction;

public interface OnSignTransactionListener {
    void onSignTransaction(Transaction transaction);
    void onSignTransaction2(int callbackId,
                            String recipient,
                            String value,
                            String nonce,
                            String gasLimit,
                            String gasPrice,
                            String payload);
}
