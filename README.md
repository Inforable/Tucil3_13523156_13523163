<h1 align="center">Tugas Kecil 3 IF2211 Strategi Algoritma</h1>
<h3 align="center">Penyelesaian Puzzle Rush Hour Menggunakan Algoritma Pathfinding</p>

## Table of Contents

- [Overview](#overview)
    - [Built With](#built-with)
- [Prerequisites](#prerequisites)
- [Installation](#installation)

## Overview
Rush Hour adalah sebuah permainan puzzle logika berbasis grid yang menantang pemain untuk menggeser kendaraan di dalam sebuah kotak agar mobil utama dapat keluar dari kemacetan melalui pintu keluar di sisi papan. 
Tujuan utama dari permainan ini adalah memindahkan mobil merah ke pintu keluar dengan jumlah langkah seminimal mungkin.
Komponen penting dari permainan ini terdiri dari:
1. Board (Papan) – Papan terdiri atas cell, yaitu sebuah singular point dari papan yang akan ditempati sebuah piece.
2. Piece – Piece adalah sebuah kendaraan di dalam papan yang memiliki posisi, ukuran, dan orientasi.
3. Primary Piece – Primary piece adalah kendaraan utama yang harus dikeluarkan dari papan.
4. Pintu Keluar – Pintu keluar adalah tempat primary piece dapat digerakkan keluar untuk menyelesaikan permainan
5. Gerakan — Gerakan yang dimaksudkan adalah pergeseran piece di dalam permainan. 

Permainan dimulai dengan papan yang dengan struktur tertentu. Pemain dapat meletakkan menggeser piece sedemikian
sehingga tidak ada piece selain primary piece yang menghalangi jalur. Setiap piece digerakan sesuai daerah kosong disekitarnya.
Puzzle dinyatakan selesai jika dan hanya jika primary piece bisa dikeluarkan.

### Build With
[Java]()

## Prerequisites
Prasyarat untuk menjalankan program ini adalah Java Programming Language.

## Installation
Jika anda ingin menjalankan program ini, anda harus mempersiapkan:
1. clone repositori ini:
   ```shell
   git clone https://github.com/Inforable/Tucil3_13523156_13523163
   ```
2. Ubah directory:
   ```shell
   cd Tucil3_13523156_13523163
   ```
3. Compile seluruh program bagi pengguna Linux:
   1. Bagi pengguna Windows:
      ```shell
       ./build.bat
      ```
   3. Bagi pengguna Linux:
      ```shell
       ./build.sh
      ```      
