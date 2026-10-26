import * as fs from "fs";

type Handler = () => void;
type StringHandler = (str: string) => void;

class WordFrequencyFramework {
    private loadEventHandlers: StringHandler[] = [];
    private doWorkEventHandlers: Handler[] = [];
    private endEventHandlers: Handler[] = [];

    registerForLoadEvent(handler: StringHandler): void {
        this.loadEventHandlers.push(handler);
    }

    registerForDoWorkEvent(handler: Handler): void {
        this.doWorkEventHandlers.push(handler);
    }

    registerForEndEvent(handler: Handler): void {
        this.endEventHandlers.push(handler);
    }

    run(pathToFile: string): void {
        for (const h of this.loadEventHandlers) {
            h(pathToFile);
        }
        for (const h of this.doWorkEventHandlers) {
            h();
        }
        for (const h of this.endEventHandlers) {
            h();
        }
    }
}

class DataStorage {
    // read data on the load event and calls back the word handlers for each non-stop word on dowork
    private data = "";
    private stopWordFilter: StopWordFilter;
    private wordEventHandlers: StringHandler[] = [];

    constructor(wfapp: WordFrequencyFramework, stopWordFilter: StopWordFilter) {
        this.stopWordFilter = stopWordFilter;
        wfapp.registerForLoadEvent(this.load);
        wfapp.registerForDoWorkEvent(this.produceWords);
    }

    private load = (pathToFile: string): void => {
        const raw = fs.readFileSync(pathToFile, "utf-8");
        this.data = raw.replace(/[\W_]+/g, " ").toLowerCase();
    };

    private produceWords = (): void => {
        for (const w of this.data.split(/\s+/)) {
            // js/ts would split also for a space
            if (w === "") continue;
            if (!this.stopWordFilter.isStopWord(w)) {
                for (const h of this.wordEventHandlers) h(w);
            }
        }
    };

    registerForWordEvent(handler: StringHandler): void {
        this.wordEventHandlers.push(handler);
    }
}

class StopWordFilter {
    // load the stop words on the load event
    private stopWords: string[] = [];

    constructor(wfapp: WordFrequencyFramework) {
        wfapp.registerForLoadEvent(this.load);
    }

    private load = (): void => {
        this.stopWords = fs.readFileSync("../stop_words.txt", "utf-8").split(",");
    };

    isStopWord(word: string): boolean {
        return word.length === 1 || this.stopWords.includes(word);
    }
}

class WordFrequencyCounter {
    private wordFreqs = new Map<string, number>();

    constructor(wfapp: WordFrequencyFramework, dataStorage: DataStorage) {
        dataStorage.registerForWordEvent(this.incrementCount);
        wfapp.registerForEndEvent(this.printFreqs);
    }

    private incrementCount = (word: string): void => {
        this.wordFreqs.set(word, (this.wordFreqs.get(word) ?? 0) + 1);
    };

    private printFreqs = (): void => {
        const sorted = [...this.wordFreqs.entries()].sort((a, b) => b[1] - a[1]);
        for (const [w, c] of sorted.slice(0, 25)) {
            console.log(w + ' - ' + c);
        }
    };
}

class WordsWithZCounter {
    // could not acceft other functions
    private count = 0;

    constructor(wfapp: WordFrequencyFramework, dataStorage: DataStorage) {
        dataStorage.registerForWordEvent(this.checkWord);
        wfapp.registerForEndEvent(this.printCount);
    }

    private checkWord = (word: string): void => {
        if (word.includes("z")) this.count++;
    };

    private printCount = (): void => {
        console.log("//////////////// below is for Z counter /////////////////")
        console.log(`Non-stop words with z: ${this.count}`);
    };
}


const wfapp = new WordFrequencyFramework();
const stopWordFilter = new StopWordFilter(wfapp);
const dataStorage = new DataStorage(wfapp, stopWordFilter);
new WordFrequencyCounter(wfapp, dataStorage);
new WordsWithZCounter(wfapp, dataStorage);
wfapp.run(process.argv[2] || '');