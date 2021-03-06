package edu.berkeley.cs186.database.recovery;

/**
 * Interface for a recovery manager.
 */
public interface RecoveryManager extends AutoCloseable {
    /**
     * Initializes the log; only called the first time the database is set up.
     */
    void initialize();

    /**
     * Write a commit record to the log, and flush it to disk before returning.
     * @param transNum transaction being committed
     */
    void commit(long transNum);

    /**
     * Writes an abort record to the log.
     * @param transNum transaction being aborted
     */
    void abort(long transNum);

    /**
     * Cleans up and ends the transaction. This method should write any needed
     * CLRs to the log, as well as the END record.
     * @param transNum transaction to finish
     */
    void end(long transNum);

    /**
     * Called before a page is flushed from the buffer cache. The log must be flushed
     * up to the pageLSN of the page before the page is flushed.
     * @param pageNum page number of page about to be flushed
     * @param pageLSN pageLSN of page about to be flushed
     */
    void pageFlushHook(long pageNum, long pageLSN);

    /**
     * Log a write to a page. Should do nothing and return -1 if the page is a log page.
    * @param transNum transaction performing the write
    * @param pageNum page number of page being written
    * @param pageOffset offset into page where write begins
    * @param before bytes starting at pageOffset before the write
    * @param after bytes starting at pageOffset after the write
    * @return LSN of record
    */
    long logPageWrite(long transNum, long pageNum, short pageOffset, byte[] before,
                      byte[] after);

    /**
     * Logs a partition allocation.
     * @param transNum transaction requesting the allocation
     * @param partNum partition number of the new partition
     * @return LSN of record
     */
    long logAllocPart(long transNum, long partNum);

    /**
     * Logs a partition free.
     * @param transNum transaction requesting the partition be freed
     * @param partNum partition number of the partition being freed
     * @return LSN of record
     */
    long logFreePart(long transNum, long partNum);

    /**
     * Logs a page allocation.
     * @param transNum transaction requesting the allocation
     * @param pageNum page number of the new page
     * @return LSN of record
     */
    long logAllocPage(long transNum, long pageNum);

    /**
     * Logs a page free.
     * @param transNum transaction requesting the page be freed
     * @param pageNum page number of the page being freed
     * @return LSN of record
     */
    long logFreePage(long transNum, long pageNum);

    /**
     * Logs a successful flush.
     * @param pageNum page number of page that was flushed
     * @return LSN of record
     */
    long logDiskIO(long pageNum);

    /**
     * Creates a savepoint for a transaction.
     * @param transNum transaction to make savepoint for
     * @param name name of savepoint
     */
    void savepoint(long transNum, String name);

    /**
     * Releases (deletes) a savepoint for a transaction.
     * @param transNum transaction to delete savepoint for
     * @param name name of savepoint
     */
    void releaseSavepoint(long transNum, String name);

    /**
     * Rolls back transaction to a savepoint
     * @param transNum transaction to partially rollback
     * @param name name of savepoint
     */
    void rollbackToSavepoint(long transNum, String name);

    /**
     * Starts the checkpointing process.
     */
    void checkpoint();

    /**
     * This method is called whenever the database starts up, and performs recovery.
     * - [something something always happens]
     */
    void restart();

    @Override
    void close();
}
