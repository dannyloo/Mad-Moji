package com.qeue.ahhh_round.activities.helpers;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;

import com.android.IabUtils.IabException;
import com.android.IabUtils.IabHelper;
import com.android.IabUtils.IabResult;
import com.android.IabUtils.Inventory;
import com.android.IabUtils.Purchase;
import com.android.IabUtils.SkuDetails;
import com.qeue.ahhh_round.events.UpdateRemoveAdsButtonEvent;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import static com.android.IabUtils.IabHelper.BILLING_RESPONSE_RESULT_ITEM_ALREADY_OWNED;

/**
 * This class allows easy integration of IAB (in-app billing).
 * It handles connecting to the Google IAB service.
 * It handles the flow of paying to remove ads.
 */
public class RemoveAdsHandler {
    private Activity activity;
    private Bus bus;
    private com.qeue.ahhh_round.stores.GameActivityStore gameActivityStore;
    private IabHelper billingService;

    public RemoveAdsHandler(Activity activity, Bus bus, com.qeue.ahhh_round.stores.GameActivityStore gameActivityStore) {
        this.activity = activity;
        this.bus = bus;

        bus.register(this);
        this.gameActivityStore = gameActivityStore;

        connectToBilling();
    }

    @Subscribe
    public void purchaseAdRemoval(com.qeue.ahhh_round.events.PurchaseAdRemovalEvent event) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    SkuDetails removeAdsSku = billingService.queryInventory(true, null, null).getSkuDetails(activity.getResources().getString(com.qeue.ahhh_round.R.string.remove_ads_sku));
                    if (removeAdsSku != null) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                        String localizedPrice = removeAdsSku.getPrice();
                        builder.setMessage("Would you like to pay " + localizedPrice + " to remove ads?\n" +
                                "\n" +
                                "Note: If you have already paid, the purchase will be restored and you won't be charged.")
                                .setNegativeButton("No", null)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        startPurchaseProcess();
                                    }
                                }).show();
                    } else {
                        purchaseError();
                    }
                } catch (IabException e) {
                    purchaseError();
                }
            }
        });
    }

    public void onDestroy() {
        if (billingService != null) {
            try {
                billingService.dispose();
            } catch (IabHelper.IabAsyncInProgressException e) {
            }
        }
        billingService = null;
    }

    public boolean handleActivityResult(int request, int response, Intent data) {
        if (billingService != null) {
            return billingService.handleActivityResult(request, response, data);
        }
        return false;
    }

    private void connectToBilling() {
        billingService = new IabHelper(activity, activity.getResources().getString(com.qeue.ahhh_round.R.string.public_key));
        billingService.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            @Override
            public void onIabSetupFinished(IabResult result) {
                bus.post(new UpdateRemoveAdsButtonEvent(result.isSuccess()));
                if (result.isSuccess()) {
                    checkIfAdsAlreadyRemoved();
                }
            }
        });
    }

    private void startPurchaseProcess() {
        try {
            billingService.launchPurchaseFlow(activity, activity.getResources().getString(com.qeue.ahhh_round.R.string.remove_ads_sku), IabHelper.ITEM_TYPE_INAPP, null, 0, new IabHelper.OnIabPurchaseFinishedListener() {
                @Override
                public void onIabPurchaseFinished(IabResult result, Purchase info) {
                    purchaseFinished(result, info);
                }
            }, null);
        } catch (IabHelper.IabAsyncInProgressException e) {
            purchaseError();
        }
    }

    private void purchaseFinished(IabResult result, Purchase info) {
        if (result.isSuccess() || result.getResponse() == BILLING_RESPONSE_RESULT_ITEM_ALREADY_OWNED) {
            new AlertDialog.Builder(activity)
                    .setMessage(com.qeue.ahhh_round.R.string.remove_ads_success_message)
                    .setPositiveButton("Okay", null).show();
            purchaseSuccess();
        } else {
            purchaseError();
        }
    }

    private void purchaseSuccess() {
        gameActivityStore.setHasPaidToRemoveAds(true);
        bus.post(new UpdateRemoveAdsButtonEvent(false));
    }

    private void purchaseError() {
        new AlertDialog.Builder(activity)
                .setMessage(com.qeue.ahhh_round.R.string.remove_ads_error_message)
                .setPositiveButton("Okay", null).show();
    }

    private void checkIfAdsAlreadyRemoved() {
        if (!gameActivityStore.hasPaidToRemoveAds()) {
            try {
                billingService.queryInventoryAsync(true, null, null, new IabHelper.QueryInventoryFinishedListener() {
                    @Override
                    public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
                        Purchase purchase = inventory.getPurchase(activity.getResources().getString(com.qeue.ahhh_round.R.string.remove_ads_sku));
                        if (purchase != null && purchase.getPurchaseState() == 0) {
                            purchaseSuccess();
                        }
                    }
                });
            } catch (IabHelper.IabAsyncInProgressException e) {
            }
        }
    }
}
