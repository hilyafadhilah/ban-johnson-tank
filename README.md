# Tugas Besar 1 IF2211 Strategi Algoritma 2021/2022

Kelompok 27

- 13520024 Hilya Fadhilah Imania
- 13520106 Roby Purnomo
- 13520155 Jundan Haris

## Deskripsi Singkat

Program ini dibuat untuk memenangkan permainan Overdrive
dengan strategi algoritma greedy.

### Bot-Bot

1. Johnson Pemadam      (`bot1`) : Greedy by Distance
2. Johnson Transformer  (`bot2`) : Greedy by Damage/Speed
3. Johnson PowerUp      (`bot3`) : Greedy by PowerUp
4. Johnson Collector    (`bot4`) : Greedy by Points
5. Johnson United       (`bot5`) : Greedy by Damage/Speed dengan pendekatan heuristik
6. Johnson Suka EMP     (`bot6`) : Greedy by Comeback (EMP, Lizard, Boost)

Bot yang terpilih adalah bot 5, Johnson United.

## Requirements dan Instalasi

Terdapat 8 program bot yang ditulis dalam bahasa Java versi 8.
Instalasi tiap bot dilakukan dengan utility Maven yang digabungkan dengan `make`.
Sebagai contoh:

```
$ cd bot1
$ make
```

Executable JAR akan di-build pada direktori `target`.

## Menjalankan Program

Rujukan: [Overdrive Github Page](https://github.com/EntelectChallenge/2020-Overdrive)

## License

[MIT](https://opensource.org/licenses/MIT)

Copyright (C) 2022, Ban Johnson (Tank)
