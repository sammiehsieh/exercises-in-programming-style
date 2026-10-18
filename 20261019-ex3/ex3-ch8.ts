import * as fs from 'fs';

const data = fs.readFileSync(process.argv[2] || '', 'utf-8').trim().toLowerCase().split(/\s+/).join(' ');
const stopWords = new Set(fs.readFileSync('../stop_words.txt', 'utf-8').split(','));

const words: string[] = [];

class Reader {
    curr = 0;
    private data: string;

    constructor(data: string) {
        this.data = data;
    }

    next(): string | null {
        if (this.curr < this.data.length) {
            const char = this.data[this.curr]!;
            this.curr = this.curr + 1;
            return char;
        }
        return null;
    }
};

const reader = new Reader(data);

function parse(reader: any, words: string[], stopWords: Set<string>) {
    let word = '';
    let char = reader.next();

    while (char !== null) {
        if (/[a-z]/.test(char)) {
            word = word + char;
        } else if (word.length > 0) {
            break;
        }
        char = reader.next();
    }

    if (word.length >= 2 && !stopWords.has(word)) {
        words.push(word);
    }

    if (char === null) { return words; }

    return parse(reader, words, stopWords);
}

const result = parse(reader, words, stopWords);

const wordFreq: Record<string, number> = {};
for (const w of result) {
    wordFreq[w] = (wordFreq[w] ?? 0) + 1;
}

Object.entries(wordFreq).sort((a, b) => a[1] - b[1]).slice(0, 25).forEach(([w, c]) => console.log(w, ' - ', c));