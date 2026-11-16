public class Main {
    public static void main(String[] args) throws InterruptedException {
        ActiveWFObject wordFreqManager = new WordFrequencyManager();
        ActiveWFObject stopWordManager = new StopWordManager();
        ActiveWFObject.send(stopWordManager, new Object[] { "init", wordFreqManager });
        ActiveWFObject storageManager = new DataStorageManager();
        ActiveWFObject.send(storageManager, new Object[] { "init", args[0], stopWordManager });
        ActiveWFObject wordFreqController = new WordFrequencyController();
        ActiveWFObject.send(wordFreqController, new Object[] { "run", storageManager });
        wordFreqManager.join();
        stopWordManager.join();
        storageManager.join();
        wordFreqController.join();
    }
}