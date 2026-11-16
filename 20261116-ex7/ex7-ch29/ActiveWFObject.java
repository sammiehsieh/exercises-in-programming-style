import java.util.concurrent.LinkedBlockingQueue;

abstract class ActiveWFObject extends Thread {
    protected LinkedBlockingQueue<Object[]> queue = new LinkedBlockingQueue<>();
    protected boolean stopMe = false;

    public ActiveWFObject() {
        // start thread when creating this class object
        this.start();
    }

    // child class decide what to do after it gets message
    protected abstract void dispatch(Object[] message);

    // keep running till message ends
    @Override
    public void run() {
        while (!stopMe) {
            try {
                Object[] message = queue.take();
                dispatch(message);
                if (message[0].equals("die")) {
                    stopMe = true;
                }
            } catch (InterruptedException e) {
                System.out.println("Error when taking words: " + e.getMessage());
            }
        }
    }

    // send message to other object by add them into queue
    public static void send(ActiveWFObject receiver, Object[] message) {
        try {
            receiver.queue.put(message);
        } catch (InterruptedException e) {
            System.out.println("Error when sending words: " + e.getMessage());
        }
    }
}
