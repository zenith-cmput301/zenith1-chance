package com.example.zenithchance.interfaces;

import com.google.android.gms.tasks.Task;

public interface LotteryService {
    void checkAndRunLotteries();
    Task<Void> runLotteryForEvent(String eventId);
}
