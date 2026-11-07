# SignalTrack — mobil operator signali o'lchash dasturi

Marshrut (trassa) bo'ylab barcha operatorlarning signalini o'lchab, har bir operator
uchun alohida interaktiv hisobot-xarita yaratadi. Hisobot bitta HTML fayl — iOS,
Android, Windows, Linux'da istalgan brauzerda ochiladi, internet talab qilmaydi,
yaqinlashtirish mumkin. PDF ham chiqarish mumkin.

## Imkoniyatlar
- GPS + signal kuchi yozib borish (RSRP/RSRQ/SINR/LTE-NR, RSSI/2G-3G), 2.5 soniyada yoki 12 m
- Dual SIM: SIM tanlab o'lchash; barcha operatorlarni o'lchash uchun marshrutda SIM almashing
  (har SIM uchun alohida hisobot bo'lagi avtomatik yaratiladi)
- Test qo'ng'iroq: raqam kiritib qo'ng'iroq — boshlanish/tugash xodisalari hisobotga tushadi
- Hisobot: operator bo'yicha alohida xarita; "Oddiy" (A'lo/Yaxshi/O'rtacha/Yomon/Juda yomon/
  Ulanmagan) va "Texnik" (RSRP diapazonlari, RSRQ, SINR, Cell ID jadvali) rejimlari
- Trassadan eng yaqin bazaviy stansiyalar (towers.csv orqali) va masofasi xaritada
- HTML hisobotni Telegram/e-mail orqali yuborish; "PDF" tugmasi — tizimli PDF saqlash

## towers.csv (bazaviy stansiyalar) — IXTYORIY lekin tavsiya etiladi
1. https://opencellid.org dan bepul akkaunt ochib, Uzbekistan (UZ) export CSV yuklab oling.
2. Faylni `towers.csv` deb nomlab, telefonda quyidagi papkaga qo'ying:
   `Android/data/uz.signaltrack/files/towers.csv`
   (yoki ilova `files` papkasiga — USB orqali kompyuterdan ham qo'ysa bo'ladi).
3. Agar qo'ymasangiz, xarita BS'siz, faqat trassa bilan ishlaydi (hisobot to'liq ishlaydi).

## Android qurilmada sozlash
- Samsung (One UI): Sozlamalar > Ilovalar > SignalTrack > Batareya > "Cheklanmagan".
  Bu o'lchash ekran o'chiq holatda ham davom etishi uchun ZARUR.
- Birinchi ochilishda GPS, telefon va bildirishnoma ruxsatlarini bering.
- GPS aniq trassa uchun ochiq bo'lsin.

## O'lchash jarayoni
1. SIMni 1-operatorga qo'yib, marshrut bo'ylab yurishingiz mumkin.
2. Keyin ilovaga qayting, "Tugatish", keyin boshqa SIMni qo'ying, "Boshlash".
   Har bir operator uchun o'lchovlar alohida saqlanadi.
3. Uyda: "Hisobot (HTML)" tugmasi — fayl Telegram/e-mail orqali yuboriladi.
   Qabul qiluvchi uni ochsa, xarita ko'rinishida to'liq hisobotni ko'radi.
4. "PDF" — cho'ntak uchun chop etilgan ko'rinish (oddiy rejimdagi grafiklar bilan).

## Talablar
- Android 12+ (API 31). Sizning Samsung A16 / Android 16 to'liq mos.
- APK yig'ish: Android Studio (Hedgehog+) oching > Build APK.

## Cheklovlar
- Bitta telefon bir vaqtda faqat o'zi tarmog'idagi operator(lar)ni o'lchaydi.
  Operatorlarni taqqoslash uchun marshrutda SIM almashtirish kerak
  (ilova buni qo'llab-quvvatlaydi: har SIM alohida bo'lim).
- Bazaviy stansiya koordinatalari OpenCellId taxminiy bazasidan olinadi
  (aniqlik odatda 100–500 m).
- Ovoz sifati (MOS) bevosita o'lchanmaydi; qo'ng'iroq xodisalari va signal
  tebranishi orqali baholanadi.
