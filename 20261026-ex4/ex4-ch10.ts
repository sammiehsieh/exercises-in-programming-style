import * as fs from 'fs';

class TheOne {
    private value: any;

    constructor(v: any) {
        this.value = v;
    }

    bind(func: (v: any) => any): TheOne {
        this.value = func(this.value);
        return this;
    }

    printme(): void {
        console.log(this.value);
    }
}

function readFile(pathToFile: string): string {
    return fs.readFileSync(pathToFile, 'utf-8');
}

function filterChars(strData: string): string {
    return strData.replace(/[\W_]+/g, ' ');
}

function normalize(strData: string): string {
    return strData.toLowerCase();
}

function scan(strData: string): string[] {
    return strData.split(/\s+/).filter(w => w.length > 0);
}

function removeStopWords(wordList: string[]): string[] {
    const stopWords = fs.readFileSync('../stop_words.txt', 'utf-8').trim().split(',');
    return wordList.filter(w => w.length > 1 && !stopWords.includes(w));
}

function frequencies(wordList: string[]): Map<string, number> {
    const wordFreqs = new Map<string, number>();
    for (const w of wordList) {
        wordFreqs.set(w, (wordFreqs.get(w) ?? 0) + 1);
    }
    return wordFreqs;
}

function sort(wordFreqs: Map<string, number>): [string, number][] {
    return [...wordFreqs.entries()].sort((a, b) => b[1] - a[1]);
}

function top25Freqs(wordFreqs: [string, number][]): string {
    let top25 = '';
    const top = wordFreqs.slice(0, 25);
    for (let i = 0; i < top.length; i++) {
        const [word, count] = top[i]!;
        top25 += word + ' - ' + count;

        if (i < top.length - 1) {
            top25 += '\n';
        }
    }
    return top25;
}

new TheOne(process.argv[2])
    .bind(readFile)
    .bind(filterChars)
    .bind(normalize)
    .bind(scan)
    .bind(removeStopWords)
    .bind(frequencies)
    .bind(sort)
    .bind(top25Freqs)
    .printme();