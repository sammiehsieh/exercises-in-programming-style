# Programming Styles Exercises

## Intro

I'm using TypeScript to implement the term frequency program.
Exercise 6 (Style #20, Plugins) is required to be in Java or C#, so I use Java for that one.

Each exercise lives in its own folder.

| Exercise | Folder name |
| -------- | ----------- |
| 1 | 20261005-ex1 |
| 2 | 20261012-ex2 |
| 3 | 20261019-ex3 |
| 4 | 20261026-ex4 |
| 5 | 20261109-ex6 |

## Environment (TypeScript, Exercises 1 to 4)

Requirement: Node.js v24 or later

1. After installing Node.js, run the command below to make sure the environment is good to go.

```bash
node -v
```

2. Confirm that it prints v24 or later. Then change to the exercise directory.

```bash
cd ./20261005-ex1
```

3. Install the dependencies:

```bash
npm i
```

4. Run the program:

```bash
node ex1.ts ../pride-and-prejudice.txt
```

| Exercise | Chapter/Question | File name |
| -------- | ---------------- | --------- |
| 1 |  -  | ex1.ts |
| 2 | Ch4 | ex2-ch4.ts |
|   | Ch5 | ex2-ch5.ts |
|   | Ch6 | ex2-ch6.ts |
| 3 | Ch7 | ex3-ch7.ts |
|   | Ch8 | ex3-ch8.ts |
| 4 | Ch9 | ex4-ch9.ts |
|   | Ch10 | ex4-ch10.ts |
|   | Ch15 | ex4-ch15.ts |

## Environment (Java, Exercise 6)

Requirement: JDK 17 or later (developed with JDK 25)

1. Check your Java version:

```bash
java -version
```

2. Change to the exercise directory:

```bash
cd ./20261109-ex6
```

3. Compile. The main program and the plugins are compiled separately from the shared interfaces, and the output goes to `out/`:

```bash
javac -d out api/*.java plugins/*.java Main.java
```

4. Run the program:

```bash
java -cp out Main ../pride-and-prejudice.txt
```
