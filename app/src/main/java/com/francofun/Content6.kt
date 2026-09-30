package com.francofun

// Content6: Unit 11 (B2 Mastery — the deep grammar that separates B1 from B2)
// + Unit 12 (Idiomes & proverbes — sound French, not translated).
// Additive only, same Lesson/Phrase shape as Content.kt.

// ─── UNIT 11 · B2 MASTERY (u11) ─────────────────────────────────────────────

val EXTRA5_LESSONS: List<Lesson> = listOf(
    Lesson("b-verbes", "🧠", "Verbes irréguliers rois", "Master pouvoir, vouloir, devoir, savoir", "Vitenzi wakubwa", listOf(
        Phrase("Je peux t'aider demain", "I can help you tomorrow", "Ninaweza kukusaidia kesho", "Naweza kukusaidia kesho", "pouvoir irregular everywhere: peux, peut, pouvons, pu.", "pouvoir: peux, peux, peut, pouvons, pouvez, peuvent. Participle pu (no agreement with avoir). Je n'ai pas pu venir.", "B2", true),
        Phrase("Il a pu finir à temps", "He managed to finish on time", "Aliweza kumaliza kwa wakati", null, "a pu = managed to (achievement).", "Past nuance: il a pu = he succeeded; il pouvait = he was able (background). Aspect in one auxiliary.", "B2"),
        Phrase("Je veux que tu viennes", "I want you to come", "Nataka uje", null, "vouloir: veux, veut, voulons; participle voulu.", "vouloir que + SUBJUNCTIVE: Je veux que tu fasses attention. Desire triggers the mood.", "B2"),
        Phrase("J'aurais voulu être là", "I would have liked to be there", "Ningalipenda kuwa hapo", null, "Conditionnel passé of vouloir = polite regret.", "J'aurais voulu / dû / pu: the regret trio. Exam essays and apologies live here.", "B2"),
        Phrase("Tu dois réviser ce soir", "You must revise tonight", "Lazima usome leo", null, "devoir: dois, doit, devons; obligation.", "devoir + infinitive = must. dû (participle) with circumflex. Je dois partir = I have to go.", "B1"),
        Phrase("Il aurait dû pleuvoir", "It should have rained", "Ingalipaswa kunyesha", null, "aurait dû = should have (didn't).", "aurait dû / aurait pu / aurait fallu: hindsight grammar. Météo + regret = B2 storytelling.", "B2"),
        Phrase("Je sais nager depuis petit", "I've known how to swim since childhood", "Najua kuogelea tangu utoto", null, "savoir = know-how. connaître = know people/places.", "savoir (facts/skills) vs connaître (people/places/things): Je sais conduire, je connais Nairobi. Tested yearly.", "B1"),
        Phrase("Sais-tu où il habite ?", "Do you know where he lives?", "Unajua anaishi wapi?", null, "Inversion with savoir sounds formal-sharp.", "Sais-tu…? / Savez-vous…? = polite information requests. Oral exam power opener.", "B1"),
        Phrase("Il faut que je parte", "I have to go", "Lazima niende", "Lazima niende", "falloir: only il faut. + subjunctive.", "il faut que + SUBJ: Il faut que tu viennes. Defective verb — no je/tu forms exist. Falloir = necessity itself.", "B2"),
        Phrase("Mieux vaut tard que jamais", "Better late than never", "Afadhali kuchelewa kuliko kukosa", null, "valoir: vaut in the proverb.", "valoir: vaux, vaut, valons. Mieux vaut + infinitive. Proverb + irregular verb = double marks.", "B2")
    ), "u11"),
    Lesson("b-discours", "🗣️", "Discours indirect", "Reported speech", "Kunukuu maneno", listOf(
        Phrase("Il dit qu'il est fatigué", "He says he's tired", "Anasema amechoka", null, "Present reporting → no tense shift.", "Reporting verb present (dit, demande): keep all tenses. Il dit qu'il viendra. Easy mode.", "B1", true),
        Phrase("Il a dit qu'il était fatigué", "He said he was tired", "Alisema alikuwa amechoka", null, "Past reporting → shift back one step.", "Past reporting shifts: présent→imparfait, passé composé→plus-que-parfait, futur→conditionnel. The B2 machine.", "B2"),
        Phrase("Elle a dit qu'elle viendrait", "She said she would come", "Alisema atakuja", null, "futur → conditionnel in reported past.", "Je viendrai → elle a dit qu'elle viendrait. Future-in-the-past lives here. Classic exam transform.", "B2"),
        Phrase("Il m'a demandé où j'habitais", "He asked me where I lived", "Aliniuliza ninaishi wapi", null, "Questions: keep the question word, shift tense.", "Où habites-tu ? → Il a demandé où j'habitais. Word order flattens: no inversion in reported questions.", "B2"),
        Phrase("Dis-moi si tu viens", "Tell me if you're coming", "Niambie kama unakuja", "Niambie kama unakam", "Yes/no questions → si (no shift with present).", "Tu viens ? → Dis-moi si tu viens. Past: Il a demandé si je viendrais. si = whether.", "B1"),
        Phrase("Le prof dit de se taire", "The teacher says to be quiet", "Mwalimu anasema tunyamaze", null, "Orders → de + infinitive.", "Tais-toi ! → Il dit de se taire. Orders collapse to de + infinitive. No subjunctive needed.", "B1"),
        Phrase("D'après lui, tout va bien", "According to him, all's well", "Kulingana naye, kila kitu kiko sawa", null, "d'après = distancing report.", "D'après / Selon + noun: report without endorsing. Journalism French, comprehension answers.", "B1"),
        Phrase("Il paraît qu'il a plu", "It seems it rained", "Inaonekana mvua ilinyesha", null, "il paraît que + indicative (rumour).", "Il paraît que (+indic) vs Il semble que (+subj). Rumour vs impression — mood marks the difference.", "B2"),
        Phrase("On raconte que le bus est parti", "They say the bus left", "Wanasema basi imeondoka", null, "on raconte: anonymous report.", "On dit que / Il se dit que / Ça se raconte: gossip grammar. Matatu-stage French, formalized.", "B1"),
        Phrase("Comme je te l'ai déjà dit", "As I already told you", "Kama nilivyokuambia", null, "comme + reported clause = reminder.", "Comme je l'ai dit… opens conclusions and warnings. Rédaction glue: Comme mentionné ci-dessus…", "B2")
    ), "u11"),
    Lesson("b-passif", "🏛️", "Voix passive et participe", "Passive + present participle", "Sauti na vitenzi", listOf(
        Phrase("Le pont a été construit en 1930", "The bridge was built in 1930", "Daraja lilijengwa 1930", null, "être + participle (agrees with subject!).", "Passive: être + past participle AGREEING with subject. Le rapport a été écrit. La loi a été votée.", "B2", true),
        Phrase("Il a été élu président", "He was elected president", "Alichaguliwa rais", null, "élire → élu. News passive.", "Passive verbs of news: élu, nommé, arrêté, condamné. Read one French headline daily = passive immersion.", "B2"),
        Phrase("En mangeant, il parle", "While eating, he talks", "Anapokula, anaongea", null, "en + -ant = simultaneous action.", "Gérondif: en mangeant, en marchant, en réfléchissant. Same subject doing two things. Elegant, compact.", "B2"),
        Phrase("C'est en forgeant qu'on devient forgeron", "Practice makes perfect (proverb)", "Mazoezi hufanya hodari", null, "c'est en + -ant: the proverb frame.", "C'est en + gerund = the how-to proverb: C'est en lisant qu'on apprend. Memorize the frame, fill the verb.", "B2"),
        Phrase("Tout en travaillant, elle étudie", "While working, she studies", "Anapofanya kazi, anasoma", null, "tout en + -ant = emphasis on simultaneity.", "tout en insisting: Elle chante tout en conduisant. Two full lives, one sentence.", "B2"),
        Phrase("Un homme sachant écouter", "A man who knows how to listen", "Mwanaume anayejua kusikiliza", null, "Participe présent replaces qui-clauses.", "sachant, ayant, étant, pouvant: compress relatives. Un élève ayant fini… = who has finished. Written elegance.", "B2"),
        Phrase("La porte ouverte, entre !", "Door open, come in!", "Mlango wazi, ingia!", null, "Detached participle: scene-setting.", "La réunion finie, on est partis. Noun + participle, no verb. Snapshot grammar for storytelling.", "B2"),
        Phrase("Fait avec amour à Kibera", "Made with love in Kibera", "Imetengenezwa kwa upendo Kibera", null, "fait + par/de: artisan passive.", "Fait main, fait avec soin, fait pour durer. Labels, menus, plaques — passive is everywhere in print.", "B1"),
        Phrase("On m'a volé mon téléphone", "My phone got stolen (on = they)", "Nimeibiwa simu yangu", "Nimeibiwa simu", "on-passive: spoken French prefers on.", "On m'a volé = I was robbed. Spoken passive: on + active verb. Use it, sound native.", "B1"),
        Phrase("Défense de fumer", "No smoking", "Kuvuta sigara marufuku", "Smoking marufuku", "Sign French: défense de + infinitive.", "Défense de / Interdit de / Prière de: the grammar of walls and doors. Read the city in French.", "A2")
    ), "u11"),
    Lesson("b-temps", "⏳", "Temps composés avancés", "Pluperfect & future perfect", "Nyakati ngumu", listOf(
        Phrase("J'avais déjà mangé", "I had already eaten", "Nilikuwa nimeshakula", null, "Plus-que-parfait: avoir/être imparfait + participle.", "J'avais fini, tu étais parti. The 'before the before'. Storytelling depth: background of background.", "B2", true),
        Phrase("Quand je suis arrivé, ils étaient partis", "When I arrived, they'd left", "Nilipofika, walikuwa wameondoka", null, "Two pasts, one earlier: PQP for the earlier.", "Sequence: main event = passé composé, earlier event = plus-que-parfait. Order on a timeline.", "B2"),
        Phrase("Si j'avais su, je serais venu", "If I'd known, I'd have come", "Kama ningalijua, ningalikuja", null, "si + PQP → conditionnel passé. Regret perfect.", "The ultimate regret formula. Si + plus-que-parfait, NEVER conditionnel after si. Tattoo it.", "B2"),
        Phrase("J'aurai fini avant midi", "I'll have finished by noon", "Nitakuwa nimemaliza kabla ya adhuhuri", null, "Futur antérieur: future of auxiliary + participle.", "J'aurai mangé, tu seras parti. 'Will have done' — deadlines, promises, threats. B2 future mastery.", "B2"),
        Phrase("Dès que tu auras fini, appelle-moi", "As soon as you've finished, call me", "Mara ukimaliza, nipigie", "Ukisha maliza, nipigie", "dès que + futur antérieur for future sequence.", "Dès que / aussitôt que / quand (future) + future perfect. The 'as soon as' machine.", "B2"),
        Phrase("Il aura oublié, c'est sûr", "He'll have forgotten, surely", "Atakuwa amesahau, hakika", null, "Futur antérieur = probability about the past.", "Il aura plu cette nuit (it must have rained). Supposition, elegant and very French.", "B2"),
        Phrase("J'avais peur d'avoir raté", "I feared I'd missed it", "Niliogopa labda nimekosa", null, "avoir + infinitive passé after emotions.", "Après avoir mangé, Avant de partir, Peur d'avoir raté: infinitive pasts compress clauses. Compact B2.", "B2"),
        Phrase("Autrefois, on vivait lentement", "In the past, life was slow", "Zamani, maisha yalikuwa polepole", null, "autrefois + imparfait: nostalgia engine.", "Autrefois, jadis, à l'époque: nostalgia markers + imparfait. Essay introductions love them.", "B1"),
        Phrase("Ça fait deux heures que j'attends", "I've been waiting two hours", "Nimesubiri kwa masaa mawili", "Nangoja masaa mbili", "ça fait + duration + PRESENT.", "Depuis-deux-heures family: Ça fait…que + present, Voilà…que + present. Ongoing = present, always.", "B1"),
        Phrase("Il y a longtemps qu'on s'est vus", "It's been long since we met", "Ni muda tangu tuonane", null, "il y a…que + passé composé.", "Il y a + duration + que + past: the reunion formula. Perfect for letters and dialogues.", "B1")
    ), "u11"),
    Lesson("b-registres", "🎩", "Registres de langue", "Formal, neutral, familiar", "Mitindo ya lugha", listOf(
        Phrase("Bonjour monsieur, comment allez-vous ?", "Hello sir, how are you? (formal)", "Habari mzee, hujambo?", null, "vouvoiement + monsieur = formal full.", "Soutenu markers: vous, inversion, ne…pas kept, prière de, veuillez. Administration, elders, exams.", "B1", true),
        Phrase("Salut, ça va ?", "Hi, you ok? (familiar)", "Sasa, uko poa?", "Niaje, uko aje?", "tutoiement + salut = familiar full.", "Familier markers: tu, salut/coucou, verlan (meuf, ouf), dropped ne. Friends ONLY — never examiners.", "A2"),
        Phrase("Prière de ne pas fumer", "Please do not smoke", "Tafadhali usivute sigara", null, "prière de + infinitive: written formal.", "Signage formal: Prière de patienter, Défense de stationner. Recognize it, never speak it.", "B1"),
        Phrase("Veuillez patienter un instant", "Please wait a moment", "Tafadhali subiri kidogo", null, "veuillez + infinitive: service formal.", "Veuillez patienter/suivre/signer. Banks, offices, exams. The polite command of institutions.", "B1"),
        Phrase("C'est kif-kif", "It's all the same (familiar)", "Ni sawa tu", "Ni same tu", "kif-kif: familiar equality.", "Familiar gems: kif-kif, bof, ouais (yeah), bosser (work), bagnole (car). Know them, place them.", "B1"),
        Phrase("Il bosse comme un fou", "He works like crazy", "Anafanya kazi kama kichaa", null, "bosser = work (familiar).", "bosser, bouffer (eat), picoler (drink): familiar verbs. bouffer at a gala dinner = disaster. Register awareness.", "B1"),
        Phrase("Je vous prie d'excuser mon retard", "Please excuse my lateness", "Nawaomba msamaha kwa kuchelewa", null, "Formal apology, full armour.", "Soutenu apology: Je vous prie de bien vouloir excuser… Layer the politeness, mean the regret.", "B2"),
        Phrase("T'inquiète, c'est rien", "Don't worry, it's nothing", "Usijali, si kitu", "Usijali, ni nothing", "Familiar comfort: dropped ne, tu.", "T'inquiète (not 'ne t'inquiète pas'), C'est rien, Y'a pas de souci. Friends comfort friends short.", "A2"),
        Phrase("En somme, tout est en ordre", "In short, all is in order", "Kwa ufupi, kila kitu kiko sawa", null, "en somme: formal conclusion.", "Formal connectors: en somme, partant, dès lors, force est de constater. Rédaction soutenue toolkit.", "B2"),
        Phrase("Bref, on s'est bien marrés", "Anyway, we had a laugh", "Kwa ufupi, tulicheka sana", "Anyway, tulicheka sana", "bref + se marrer: familiar close.", "Bref (anyway), se marrer (laugh hard), délirer (go wild). Story endings with friends.", "B1")
    ), "u11"),
    Lesson("b-subj2", "🌀", "Subjonctif avancé", "Emotion, doubt & necessity", "Subjonctif ya juu", listOf(
        Phrase("J'ai peur qu'il pleuve", "I'm afraid it may rain", "Ninaogopa mvua itanyesha", null, "Emotion + que → subjunctive.", "Emotion family: avoir peur que, être content que, regretter que, craindre que → SUBJ. Feelings bend verbs.", "B2", true),
        Phrase("Il est temps que tu partes", "It's time you left", "Ni wakati uondoke", null, "il est temps que → subjunctive.", "Time-pressure triggers: il est temps que, il est urgent que + subj. Exam-morning French.", "B2"),
        Phrase("Qu'il vienne ou non, on part", "Whether he comes or not, we leave", "Aje asije, tunaondoka", null, "que…ou non: concession mastery.", "Que + subj…ou non: Qu'il pleuve ou non. Indifference, elegantly fenced.", "B2"),
        Phrase("Pourvu qu'il fasse beau", "Hopefully it'll be nice", "Tumai hali itakuwa nzuri", null, "pourvu que = hoping, subjunctive.", "Pourvu que + subj: the hope clause. Pourvu qu'on gagne le derby ! Saturday French.", "B2"),
        Phrase("Sans qu'il s'en rende compte", "Without him realizing", "Bila yeye kutambua", null, "sans que + subjunctive.", "sans que (+ne explétif optional): Sans qu'il (ne) pleuve. Formal negative purpose.", "B2"),
        Phrase("Le seul qui sache la vérité", "The only one who knows the truth", "Pekee anayejua ukweli", null, "le seul qui → subjunctive (sache!).", "Superlative triggers: le seul/premier/dernier/meilleur + qui/que → subj. le meilleur film que j'aie vu.", "B2"),
        Phrase("Je ne pense pas qu'il vienne", "I don't think he'll come", "Sidhani atakuja", null, "Negative opinion → subjunctive.", "penser/croire affirmative = INDIC (je pense qu'il vient); negative/interrogative = SUBJ. The flip.", "B2"),
        Phrase("Doute-t-il qu'elle soit là ?", "Does he doubt she's there?", "Ana shaka kama yuko hapo?", null, "Questioned opinion → subjunctive.", "Penses-tu qu'il vienne ? vs Tu penses qu'il vient. Question mark bends the mood.", "B2"),
        Phrase("Vive les vacances !", "Long live the holidays!", "Maisha marefu likizo!", null, "Standalone subjunctive: wishes.", "Vive…, Qu'il/Qu'elle + subj: Qu'elle réussisse ! The ceremonial mood — toasts, blessings, slogans.", "B2"),
        Phrase("Il vaut mieux que tu dormes", "You'd better sleep", "Afadhali ulale", "Afadhali ulale", "valoir mieux que → subjunctive.", "Il vaut mieux que / Il est préférable que + subj. Advice with grammar muscle.", "B2")
    ), "u11"),

// ─── UNIT 12 · IDIOMES & PROVERBES (u12) ────────────────────────────────────
// Sound French instead of translated: body, animals, food, then the
// proverbs that crown compositions and conversations.

    Lesson("i-corps", "🖐️", "Idiotismes du corps", "Body idioms", "Misemo ya mwili", listOf(
        Phrase("Ça me casse les pieds", "That annoys me (familiar)", "Hiyo inanikasirisha", "Hiyo inanibore", "casser les pieds = annoy. Familiar, safe.", "Body-annoyance set: casser les pieds, prendre la tête, taper sur les nerfs. Familiar — friends, never officials.", "B1", true),
        Phrase("J'ai le cœur qui bat", "My heart's pounding", "Moyo wangu unadunda", null, "Avoir + body part = feelings.", "avoir le cœur qui bat, avoir la gorge serrée, avoir les jambes qui tremblent. Body states carry emotion.", "B1"),
        Phrase("Tu me tends les bras ?", "Are you welcoming me?", "Unanikaribisha?", null, "tendre les bras = welcome warmly.", "Bras idioms: tendre les bras, baisser les bras (give up), avoir le bras long (connections). Three, one body part.", "B1"),
        Phrase("Il a la tête sur les épaules", "He's level-headed", "Ana akili timamu", null, "Head idioms = character judgments.", "avoir la tête sur les épaules (sensible), perdre la tête (panic/love), tenir tête (stand up to). Character in heads.", "B1"),
        Phrase("On se serre les coudes", "We stick together", "Tunashikana", "Tunashikana", "coudes serrés = solidarity.", "Solidarity set: se serrer les coudes, mettre la main à la pâte, être dans le même bateau. Chama spirit, French words.", "B1"),
        Phrase("J'en ai plein le dos", "I'm fed up (familiar)", "Nimechoka kabisa", "Nimechoka kabisa", "plein le dos = back full = fed up.", "Fed-up ladder: j'en ai marre (standard) → plein le dos → ras-le-bol (strong). Climb carefully by company.", "B1"),
        Phrase("Ça m'a donné des frissons", "It gave me chills", "Ilinipa hamaki", null, "frissons = chills, fear or awe.", "donner des frissons, avoir la chair de poule. Horror stories and derby finals share vocabulary.", "B1"),
        Phrase("Il voit tout en noir", "He sees everything bleakly", "Anaona kila kitu kibaya", null, "voir en noir vs voir la vie en rose.", "Colour-mood pair: voir tout en noir (pessimist) vs voir la vie en rose (optimist). Piaf reference = culture.", "B1"),
        Phrase("Prends ton courage à deux mains", "Take courage (both hands)", "Jipe moyo", null, "courage à deux mains: exam-morning line.", "Prendre son courage à deux mains + infinitive. The night-before-KCSE sentence.", "B1"),
        Phrase("Les doigts dans le nez", "Easily (fingers in nose)", "Rahisi sana", "Easy tu", "Familiar ease, playful.", "Faire quelque chose les doigts dans le nez. Boast gently after easy wins — friends only.", "B1")
    ), "u12"),
    Lesson("i-animaux", "🦁", "Idiotismes animaux", "Animal idioms", "Misemo ya wanyama", listOf(
        Phrase("Fier comme un paon", "Proud as a peacock", "Mwenye kiburi kama tausi", null, "comme un + animal = character similes.", "Simile farm: fier comme un paon, têtu comme une mule, doux comme un agneau, rusé comme un renard.", "B1", true),
        Phrase("Poser un lapin", "To stand someone up", "Kumwacha mtu mataa", "Kumwacha mtu dry", "Rabbit idiom = no-show. Essential.", "Poser un lapin à quelqu'un. Dating, meetings, chama: the no-show verb. Victims say: On m'a posé un lapin.", "B1"),
        Phrase("Avoir un chat dans la gorge", "To have a frog in the throat", "Koo limekauka", null, "chat = frog here. Throat idiom.", "avoir un chat dans la gorge. Oral exam rescue: Pardon, j'ai un chat dans la gorge — buys water + sympathy.", "B1"),
        Phrase("Donner sa langue au chat", "To give up guessing", "Kukata tamaa kubahatisha", null, "Game idiom: je donne ma langue au chat.", "Quizzes, riddles, guessing games: Je donne ma langue au chat ! Then hear the answer. Playful surrender.", "A2"),
        Phrase("Un froid de canard", "Freezing cold", "Baridi kali", "Baridi kali", "canard = duck = bitter cold.", "Weather animals: un froid de canard, un temps de chien. Limuru mornings deserve them.", "B1"),
        Phrase("Être le dindon de la farce", "To be the fall guy", "Kuwa mjinga wa mzaha", null, "dindon = turkey = duped.", "Farce vocabulary: le dindon, le bouc émissaire, payer les pots cassés. Office politics, French style.", "B2"),
        Phrase("Petit à petit, l'oiseau fait son nid", "Slowly the bird builds its nest", "Polepole ndege hujenga kiota", "Mora mora", "Patience proverb, exam-gold.", "Effort proverbs: Petit à petit… / Petit ruisseau fait grande rivière. Conclusions about progress.", "B1"),
        Phrase("Quand le chat n'est pas là", "When the cat's away…", "Paka asipokuwa…", null, "…les souris dansent. Know the ending.", "Quand le chat n'est pas là, les souris dansent. Classroom humour: say it when the prof steps out.", "A2"),
        Phrase("Avoir le cafard", "To feel blue", "Kujisikia vibaya", "Kufeel low", "cafard = cockroach = blues.", "avoir le cafard, broyer du noir. Homesickness, rainy Sundays: name the feeling in French.", "B1"),
        Phrase("La cerise sur le gâteau", "The cherry on top", "Bora zaidi", "Cherry on top", "cerise: the bonus idiom.", "C'est la cerise sur le gâteau. Good news pile-ups: promotion + raise + praise = cerise.", "B1")
    ), "u12"),
    Lesson("i-bouche", "🍽️", "Idiotismes gourmands", "Food idioms", "Misemo ya chakula", listOf(
        Phrase("C'est du gâteau", "It's a piece of cake", "Ni rahisi", "Ni easy", "gâteau = easy. Post-exam line.", "Easy set: C'est du gâteau, C'est du tout cuit, Les doigts dans le nez. Victory vocabulary.", "A2", true),
        Phrase("Raconter des salades", "To tell tall tales", "Kusema uongo mwingi", "Kudanganya", "salades = lies. Familiar.", "Lie set: raconter des salades, des bobards, des craques. Matatu stories deserve the word.", "B1"),
        Phrase("Mettre son grain de sel", "To put in one's two cents", "Kuingilia maneno", "Kuingilia story", "grain de sel = unsolicited opinion.", "Je mets mon grain de sel… + opinion. Meetings, family debates: announce the intrusion politely.", "B1"),
        Phrase("La moutarde me monte au nez", "My temper's rising", "Hasira inanipanda", null, "moutarde-nez = anger rising.", "Anger food: la moutarde monte, la soupe au lait (quick temper), bouillir de colère. Argue in French, stay tasty.", "B1"),
        Phrase("Être soupe au lait", "To have a quick temper", "Kuwa na hasira za haraka", null, "Milk-soup: boils instantly.", "Character via food: soupe au lait (quick temper), bonne pâte (kind soul), cordon-bleu (great cook).", "B1"),
        Phrase("Couper la poire en deux", "To meet halfway", "Kukutana katikati", "Kusplit katikati", "pear-halving = compromise.", "Bargaining close: Coupons la poire en deux ! Market French + idiom = vendor smiles, better price.", "B1"),
        Phrase("Vouloir le beurre et l'argent du beurre", "To want it all", "Kutaka vyote", "Kutaka kila kitu", "butter + butter money: greed proverb.", "Debate weapon: Tu veux le beurre et l'argent du beurre ! Limits, budgets, chama loans — deploy.", "B1"),
        Phrase("C'est la fin des haricots", "It's hopeless (playful)", "Hakuna matumaini (kichekesho)", null, "haricots: the playful disaster.", "Mild despair: C'est la fin des haricots ! Stronger: C'est la cata. Laughing at disaster = very French.", "B1"),
        Phrase("Manger sur le pouce", "To grab a quick bite", "Kula haraka", "Kula fast fast", "pouce = thumb = quick food.", "Manger sur le pouce, un café gourmand, la pause déj. Workday food French.", "A2"),
        Phrase("Ivresse du succès", "Drunk on success", "Kulewa na mafanikio", null, "ivresse = intoxication, literal or not.", "L'ivresse du succès / de la victoire. Derby wins and KCSE results share the word.", "B2")
    ), "u12"),
    Lesson("i-proverbes", "📜", "Proverbes", "Proverbs that crown essays", "Methali", listOf(
        Phrase("Petit à petit, l'oiseau fait son nid", "Little by little, the bird builds its nest", "Polepole ndege hujenga kiota", "Mora mora", "Progress proverb. Conclusions about learning.", "Use: essays on effort, speeches, advice. Kenyan twin: Haraka haraka haina baraka — quote BOTH, bilingual flex.", "B1", true),
        Phrase("Qui vivra verra", "Who lives shall see", "Mwenye kuishi ataona", "Time will tell", "Patience proverb, three words.", "Debates, predictions, derby arguments: Qui vivra verra ! Short, final, unanswerable.", "B1"),
        Phrase("L'habit ne fait pas le moine", "Clothes don't make the monk", "Mavazi hayamtengenezi mtawa", "Sura si kila kitu", "Anti-judgment proverb.", "Essays on appearances, social media, prejudice: L'habit ne fait pas le moine. Instant depth.", "B1"),
        Phrase("Tel père, tel fils", "Like father, like son", "Kama baba, kama mwana", null, "Family resemblance, fate or joke.", "Telle mère, telle fille. Family essays,足球 genes debates: Les Kipchoge du Kenya — tel père, tel fils ?", "A2"),
        Phrase("Après la pluie, le beau temps", "After rain, sunshine", "Baada ya mvua, jua", "Baada ya shida, raha", "Hope proverb. Hardship essays.", "Difficult topics (loss, failure, drought): close with Après la pluie… Examiners remember endings.", "B1"),
        Phrase("Mieux vaut prévenir que guérir", "Prevention beats cure", "Kinga ni bora kuliko tiba", null, "Health/environment essays.", "Mieux vaut + infinitive: Mieux vaut tard que jamais. Health, safety, environment topics.", "B1"),
        Phrase("Les grands esprits se rencontrent", "Great minds think alike", "Akili kubwa hukutana", null, "Agreement delight, social glue.", "Someone says your idea: Les grands esprits se rencontrent ! Instant friendship French.", "A2"),
        Phrase("À cheval donné, on ne regarde pas les dents", "Don't look a gift horse in the mouth", "Zawadi haichunguzwi meno", null, "Gratitude proverb.", "Gifts, scholarships, bursaries: accept with À cheval donné… Gratitude + proverb = grace.", "B1"),
        Phrase("Pierre qui roule n'amasse pas mousse", "Rolling stone gathers no moss", "Jiwe linaloviringishwa halikusanyi ukungu", null, "Roots vs wandering debate.", "Debate both sides: freedom (rouler) vs stability (mousse). Balanced argument = full marks.", "B2"),
        Phrase("En avril, ne te découvre pas d'un fil", "April: keep a thread on (weather wisdom)", "Aprili: usivue nguo", null, "Seasonal proverb, rhyme included.", "Weather wisdom rhymes: En mai, fais ce qu'il te plaît. Long-rains April in Nairobi fits perfectly.", "B1")
    ), "u12")
)
