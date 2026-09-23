package com.francofun

/**
 * Fully offline conversation engine. No API key, no internet, no server.
 * Each scenario is a small scripted dialogue tree: Simba speaks a French line,
 * and the learner's typed/spoken French is fuzzy-matched against good example
 * replies so Simba can react and gently offer a better phrasing — all
 * computed on-device, instantly.
 */

/** §6.1 registers: Débutant (present, short, generous) vs Intermédiaire (passé composé + futur proche, natural length). */
enum class Reg(val label: String) {
    DEBUTANT("Débutant"),
    INTERMEDIAIRE("Intermédiaire")
}

/**
 * §6.2 branching: when the learner's reply matches [match], Simba answers
 * with [reply] instead of the next script turn — then the script collapses
 * back to its shared line, so replays feel alive without a full tree.
 */
data class Branch(val match: List<String>, val reply: Turn)

data class Turn(
    val fr: String,
    val en: String,
    val sw: String,
    val sheng: String? = null,
    val expects: List<String> = emptyList(), // good example replies (French), fuzzy-matched
    val word: String? = null,                // "french = meaning" vocab nugget
    val branches: List<Branch> = emptyList()
) {
    fun help(lang: HelpLang) = when (lang) {
        HelpLang.ENGLISH -> en
        HelpLang.SWAHILI -> sw
        HelpLang.SHENG -> sheng ?: sw
    }
}

data class Script(val scenario: Scenario, val beginner: List<Turn>, val intermediate: List<Turn> = emptyList())

private fun T(fr: String, en: String, sw: String, sheng: String? = null, expects: List<String> = emptyList(), word: String? = null, branches: List<Branch> = emptyList()) =
    Turn(fr, en, sw, sheng, expects, word, branches)

val SCRIPTS: List<Script> = listOf(
    Script(Scenario.FREE, listOf(
        T("Salut ! Moi c'est Simba. Comment tu t'appelles ?", "Hi! I'm Simba. What's your name?", "Habari! Mimi ni Simba. Jina lako nani?", "Niaje! Mimi ni Simba. Unaitwa nani?",
            listOf("Je m'appelle", "Moi c'est"), word = "s'appeler = to be called"),
        T("Enchanté ! Comment ça va aujourd'hui ?", "Nice to meet you! How are you today?", "Nimefurahi! Habari yako leo?", "Poa sana! Uko aje leo?",
            listOf("ça va bien", "je vais bien", "ça va")),
        T("Super ! Tu es étudiant ou tu travailles ?", "Great! Are you a student or do you work?", "Vizuri! Wewe ni mwanafunzi au unafanya kazi?", "Poa! Uko campus ama unafanya job?",
            listOf("je suis étudiant", "j'étudie", "je travaille"), word = "un étudiant = a student"),
        T("Ah, intéressant ! Tu habites où, à Nairobi ?", "Ah interesting! Where do you live, in Nairobi?", "Ah, inavutia! Unaishi wapi, Nairobi?", "Ah poa! Unakaa wapi, Nairobi?",
            listOf("j'habite à", "je suis à")),
        T("Qu'est-ce que tu aimes faire le week-end ?", "What do you like to do on weekends?", "Unapenda kufanya nini wikendi?", "Unapenda kufanya nini wikendi?",
            listOf("j'aime", "je joue", "j'écoute"), word = "le week-end = the weekend"),
        T("Moi j'aime bien un bon nyama choma le samedi ! Et toi, tu as faim maintenant ?", "I love good nyama choma on Saturdays! And you, are you hungry right now?", "Mimi napenda nyama choma vizuri Jumamosi! Na wewe, una njaa sasa?", "Mimi napenda nyama choma poa Jumamosi! Na wewe, una njaa sasa?",
            listOf("j'ai faim", "je n'ai pas faim")),
        T("Haha, d'accord ! Tu préfères le thé ou le café ?", "Haha okay! Do you prefer tea or coffee?", "Haha sawa! Unapenda chai au kahawa?", "Haha sawa! Unapenda chai ama kahawa?",
            listOf("je préfère", "j'aime le")),
        T("Hé, entre amis on se chambre un peu… tu veux une petite vanne ? 😏", "Hey, friends roast each other a little… want a gentle roast? 😏", "Hé, marafiki huchomana kidogo… unataka mzaha? 😏", "Hé, mabeshte huchomana… unataka kuchomwa kidogo? 😏",
            listOf("oui", "vas-y", "d'accord", "non")),
        T("Ton français avance comme un matatu dans les embouteillages : lentement, mais il arrivera ! 😄", "Your French moves like a matatu in traffic: slowly, but it'll get there! 😄", "Kifaransa chako kinaenda kama matatu kwa foleni: polepole, lakini kitafika! 😄", "French yako inaenda kama matatu kwa jam: polepole, lakini itafika! 😄",
            listOf("haha", "merci", "très drôle"), word = "les embouteillages = traffic jam"),
        T("Bien joué, tu parles vraiment bien français ! On continue une autre fois ?", "Well done, you really speak French well! Shall we continue another time?", "Umefanya vizuri, unaongea Kifaransa vizuri kabisa! Tuendelee wakati mwingine?", "Umefanya poa, unaongea French fiti! Tuendelee wakati mwingine?",
            listOf("oui", "à bientôt", "d'accord"))
    ),
        intermediate = listOf(
            T("Salut ! On s'est déjà parlé la semaine dernière, non ? Qu'est-ce que tu as fait depuis ?", "Hi! We already talked last week, right? What have you done since?", "Niaje! Tuliongea wiki iliyopita, sivyo? Umefanya nini tangu hapo?", "Niaje! Tuliongea last week, sivyo? Umefanya nini since then?",
                listOf("j'ai travaillé", "j'ai étudié", "je n'ai rien fait"), word = "depuis = since"),
            T("Ah oui ? Et tu vas continuer comme ça cette semaine ?", "Oh yeah? And are you going to keep that up this week?", "Oh yeah? Na utaendelea hivyo wiki hii?", "Oh yeah? Na utaendelea hivyo hii week?",
                listOf("oui", "je vais essayer", "on verra")),
            T("Moi, hier soir j'ai regardé un match incroyable avec mes potes.", "Me, last night I watched an amazing match with my buddies.", "Mimi, jana usiku niliangalia mechi kali na mabeshte.", "Mimi, jana usiku niliwatch game kali na mabeshte.",
                listOf("qui a gagné", "c'était quel match")),
            T("Mon équipe a gagné deux à zéro ! La semaine prochaine, on va fêter ça.", "My team won two-nil! Next week we're going to celebrate.", "Timu yangu imeshinda mbili sifuri! Wiki ijayo tutasherehekea.", "Team yangu imeshinda mbili bila! Next week tutasherehekea.",
                listOf("félicitations", "quelle équipe")),
            T("Bon, je dois y aller, mais on se refait ça bientôt, d'accord ?", "Well, I've got to go, but let's do this again soon, okay?", "Sawa, lazima niende, lakini turudie hivi karibuni, sawa?", "Sawa, lazima niende, lakini turudie soon, sawa?",
                listOf("d'accord", "à bientôt", "avec plaisir"))
        )
    ),
    Script(Scenario.CAFE, listOf(
        T("Bonjour ! Bienvenue au café. Une table pour combien de personnes ?", "Hello! Welcome to the café. A table for how many people?", "Habari! Karibu kwenye mkahawa. Meza ya watu wangapi?", "Mambo! Karibu kwa café. Meza ya watu wangapi?",
            listOf("une personne", "deux personnes", "pour un")),
        T("Parfait, par ici. Vous voulez boire quelque chose ?", "Perfect, this way. Would you like something to drink?", "Vizuri, hapa. Unataka kunywa kitu?", "Poa, hapa. Unataka kunywa kitu?",
            listOf("un café", "de l'eau", "un thé"), word = "boire = to drink"),
        T("Et pour manger ? Nous avons du pain, des croissants et des salades.", "And to eat? We have bread, croissants and salads.", "Na kwa kula? Tuna mkate, ma-croissant na saladi.", "Na kwa kula? Tuko na mkate, ma-croissant na saladi.",
            listOf("je voudrais", "je prends")),
        T("Excellent choix ! Ce sera tout ?", "Excellent choice! Will that be all?", "Chaguo zuri! Ni hayo tu?", "Chaguo poa! Ni hiyo tu?",
            listOf("oui", "c'est tout", "non"),
            branches = listOf(
                Branch(listOf("non", "pas encore", "autre chose"),
                    T("Pas de problème, prenez votre temps ! Je reviens dans une minute.", "No problem, take your time! I'll be back in a minute.", "Hamna shida, chukua muda wako! Nitarudi baada ya dakika.", "Hamna shida, chukua time yako! Natarudi after dakika."))
            )),
        T("D'accord, ça arrive tout de suite !", "Alright, coming right up!", "Sawa, inakuja sasa hivi!", "Sawa, inakam sasa hivi!"),
        T("Voilà ! Bon appétit. C'est bon ?", "Here you go! Enjoy your meal. Is it good?", "Karibu! Chakula chema. Ni kitamu?", "Karibu! Kula poa. Ni tamu?",
            listOf("c'est délicieux", "c'est très bon"), word = "délicieux = delicious"),
        T("Je suis content que ça vous plaise ! Vous voulez l'addition ?", "I'm happy you like it! Do you want the bill?", "Nimefurahi unapenda! Unataka bili?", "Nimefurahi unapenda! Unataka bill?",
            listOf("oui", "l'addition s'il vous plaît")),
        T("Voici l'addition. Merci de votre visite, à bientôt !", "Here's the bill. Thanks for coming, see you soon!", "Hii hapa bili. Asante kwa kutembelea, tuonane!", "Hii hapa bill. Asante kwa kuja, tuonane!",
            listOf("merci", "au revoir"))
    ),
        intermediate = listOf(
            T("Bonsoir ! Vous avez réservé ? On est complets ce soir, mais je vais vous trouver une table.", "Good evening! Did you book? We're full tonight, but I'll find you a table.", "Habari! Uliweka meza? Tumejaa leo, lakini nitakutafutia meza.", "Mambo! Uli-book meza? Tumejaa leo, lakini nitakutafutia meza.",
                listOf("oui, j'ai réservé", "non", "une table pour deux")),
            T("Suivez-moi, je vous ai gardé la meilleure place, près de la fenêtre.", "Follow me, I kept you the best seat, by the window.", "Nifuate, nimekuwekea sehemu nzuri, karibu na dirisha.", "Nifuate, nimekuwekea place poa, karibu na window.",
                listOf("merci beaucoup", "c'est parfait")),
            T("Vous avez déjà dîné ici la semaine dernière, non ? Qu'est-ce qui vous a plu ?", "You already had dinner here last week, right? What did you like?", "Ulishakula hapa wiki iliyopita, sivyo? Nini kilikupendeza?", "Ulishakula hapa last week, sivyo? Nini ilikupendeza?",
                listOf("le poulet", "tout était bon", "je ne m'en souviens plus")),
            T("Excellent choix, comme d'habitude ! Et comme boisson, toujours la même chose ?", "Excellent choice, as usual! And to drink, the usual?", "Chaguo zuri, kama kawaida! Na kunywa, kama kawaida?", "Chaguo poa, kama kawa! Na kunywa, ile ile?",
                listOf("oui", "non, je vais changer")),
            T("Parfait, j'apporte ça tout de suite. Et après, vous prendrez un dessert ?", "Perfect, I'll bring that right away. And afterwards, will you have dessert?", "Vizuri, nitaleta sasa hivi. Na baadaye, utachukua dessert?", "Poa, nitaleta saa hii. Na baadaye, utachukua dessert?",
                listOf("oui", "non merci", "peut-être"))
        )
    ),
    Script(Scenario.MARKET, listOf(
        T("Bonjour ! Venez voir, j'ai de bons fruits aujourd'hui !", "Hello! Come look, I have good fruit today!", "Habari! Njoo uone, nina matunda mazuri leo!", "Mambo! Njoo uone, niko na matunda poa leo!",
            listOf("bonjour", "je regarde")),
        T("Vous cherchez quelque chose en particulier ?", "Are you looking for something in particular?", "Unatafuta kitu maalum?", "Unasaka kitu fulani?",
            listOf("je cherche", "je voudrais")),
        T("Ça, c'est cent francs le kilo.", "That's a hundred francs a kilo.", "Hiyo ni faranga mia moja kwa kilo.", "Hiyo ni faranga mia moja kwa kilo.",
            listOf("c'est trop cher", "c'est combien", "vous avez moins cher"), word = "un kilo = a kilo"),
        T("D'accord, je peux vous faire un petit prix. Quatre-vingts francs ?", "Okay, I can give you a small discount. Eighty francs?", "Sawa, nitakupunguzia kidogo. Faranga themanini?", "Sawa, nitakushushia kidogo. Faranga themanini?",
            listOf("d'accord", "je prends"),
            branches = listOf(
                Branch(listOf("c'est trop cher", "non", "trop cher"),
                    T("Bon, bon… soixante-dix francs, dernier prix, juste pour vous !", "Okay, okay… seventy francs, last price, just for you!", "Sawa, sawa… faranga sabini, bei ya mwisho, kwa ajili yako tu!", "Sawa, sawa… faranga sabini, bei ya mwisho, kwa ajili yako tu!"))
            )),
        T("Vous voulez autre chose ?", "Do you want anything else?", "Unataka kitu kingine?", "Unataka kitu ingine?",
            listOf("non merci", "oui", "c'est tout")),
        T("Vous payez comment, en espèces ou par mobile ?", "How are you paying, cash or mobile?", "Unalipa vipi, kwa pesa taslimu au kwa simu?", "Unalipa aje, cash au M-Pesa?",
            listOf("par mobile", "en espèces"), word = "en espèces = in cash"),
        T("Parfait. Merci beaucoup, bonne journée !", "Perfect. Thank you very much, have a good day!", "Vizuri. Asante sana, uwe na siku njema!", "Poa. Asante sana, uwe na day poa!",
            listOf("merci", "au revoir"))
    ),
        intermediate = listOf(
            T("Ah, vous revenez ! La dernière fois vous avez pris des mangues, non ?", "Ah, you're back! Last time you took mangoes, right?", "Ah, umerudi! Mara ya mwisho ulichukua maembe, sivyo?", "Ah, umerudi! Last time ulichukua maembe, sivyo?",
                listOf("oui", "vous avez bonne mémoire"), word = "la dernière fois = last time"),
            T("Aujourd'hui les avocats sont mûrs, je les ai cueillis ce matin.", "Today the avocados are ripe, I picked them this morning.", "Leo maparachichi yameiva, nimeyachuma asubuhi.", "Leo maparachichi yameiva, nimeyachuma asubuhi.",
                listOf("je vais en prendre", "c'est combien le kilo")),
            T("Cent cinquante le kilo, mais pour vous, je fais cent vingt.", "One-fifty a kilo, but for you, I'll do one-twenty.", "Mia hamsini kwa kilo, lakini kwako, mia ishirini.", "Mia hamsini kwa kilo, lakini kwako, mia ishirini.",
                listOf("d'accord", "c'est encore cher")),
            T("Vous paierez la prochaine fois qu'on se verra, on se connaît maintenant !", "You'll pay next time we see each other, we know each other now!", "Utalipa wakati ujao tutakapoona, tunajuana sasa!", "Utalipa next time tukionana, tunajuana sasa!",
                listOf("merci", "c'est gentil")),
            T("Allez, à la semaine prochaine, et amenez vos amis !", "See you next week, and bring your friends!", "Hadi wiki ijayo, na ulete marafiki zako!", "Hadi next week, na ulete mabeshte zako!",
                listOf("à bientôt", "promis"))
        )
    ),
    Script(Scenario.TRAVEL, listOf(
        T("Bonjour, je peux vous aider ?", "Hello, can I help you?", "Habari, naweza kukusaidia?", "Mambo, naweza kukusaidia?",
            listOf("je cherche", "excusez-moi")),
        T("D'accord ! Vous cherchez quoi exactement ?", "Okay! What exactly are you looking for?", "Sawa! Unatafuta nini hasa?", "Sawa! Unasaka nini hasa?",
            listOf("la gare", "un taxi", "l'hôtel")),
        T("C'est facile : tout droit, puis à gauche.", "It's easy: straight ahead, then left.", "Ni rahisi: nenda moja kwa moja, kisha kushoto.", "Ni rahisi: enda straight, kisha kushoto.",
            listOf("merci", "c'est loin"), word = "à gauche = to the left"),
        T("Non, ce n'est pas loin, cinq minutes à pied.", "No, it's not far, five minutes on foot.", "Hapana, si mbali, dakika tano kwa miguu.", "Hapana, si mbali, dakika tano kwa miguu.",
            listOf("merci beaucoup", "d'accord")),
        T("Vous voyagez seul ou en groupe ?", "Are you travelling alone or in a group?", "Unasafiri peke yako au na kikundi?", "Unasafiri solo au na wasee?",
            listOf("je voyage seul", "en groupe")),
        T("Bon voyage alors ! Vous avez votre passeport ?", "Have a good trip then! Do you have your passport?", "Safari njema basi! Una pasipoti yako?", "Safari poa basi! Una pasi yako?",
            listOf("oui", "j'ai mon passeport")),
        T("Parfait, tout est en ordre. Bon voyage !", "Perfect, everything is in order. Safe travels!", "Vizuri, kila kitu kiko sawa. Safari njema!", "Poa, kila kitu ni sawa. Safari njema!",
            listOf("merci", "au revoir"))
    ),
        intermediate = listOf(
            T("Vous êtes déjà venu dans cette ville ? Vous connaissez un peu ?", "Have you already been to this city? Do you know it a bit?", "Umeshakuja mji huu? Unaujua kidogo?", "Umeshakuja hii town? Unaijua kidogo?",
                listOf("oui", "non, c'est la première fois")),
            T("Dans ce cas, prenez le bus numéro douze, il s'arrête juste devant la gare.", "In that case, take bus twelve, it stops right in front of the station.", "Kwa hivyo, panda basi nambari kumi na mbili, inasimama mbele ya stesheni.", "Kwa hivyo, panda bus nambari kumi na mbili, inasimama mbele ya stage.",
                listOf("merci", "et après")),
            T("Ensuite vous êtes descendu au troisième arrêt, et l'hôtel sera sur votre droite.", "Then you get off at the third stop, and the hotel will be on your right.", "Kisha utashuka kituo cha tatu, na hoteli itakuwa kulia kwako.", "Kisha utashuka stage ya tatu, na hoteli itakuwa right yako.",
                listOf("c'est loin à pied", "d'accord")),
            T("À pied, comptez vingt bonnes minutes. Il a plu ce matin, alors attention aux flaques !", "On foot, allow a good twenty minutes. It rained this morning, so watch the puddles!", "Kwa miguu, hesabu dakika ishirini nzuri. Mvua ilinyesha asubuhi, kwa hivyo angalia madimbwi!", "Kwa miguu, hesabu dakika ishirini poa. Mvua ilinyesha asubuhi, so angalia madimbwi!",
                listOf("merci beaucoup", "je vais prendre un taxi")),
            T("Bon choix par ce temps ! Bon séjour parmi nous !", "Good choice in this weather! Enjoy your stay with us!", "Chaguo zuri kwa hali hii! Karibu ukae nasi!", "Chaguo poa kwa hii weather! Karibu ukae nasi!",
                listOf("merci", "au revoir"))
        )
    ),
    Script(Scenario.MEET, listOf(
        T("Salut ! Je ne crois pas qu'on se connaisse. Je m'appelle Simba.", "Hi! I don't think we've met. I'm Simba.", "Niaje! Sidhani tunajuana. Mimi ni Simba.", "Niaje! Sidhani tunajuana. Mimi ni Simba.",
            listOf("je m'appelle", "moi c'est"), word = "se connaître = to know each other"),
        T("Tu es nouveau ici ou tu viens souvent ?", "Are you new here or do you come often?", "Wewe ni mgeni hapa au unakuja mara kwa mara?", "Wewe ni mgeni hapa au unakuja mara kwa mara?",
            listOf("je suis nouveau", "je viens souvent")),
        T("Qu'est-ce que tu étudies ?", "What do you study?", "Unasoma nini?", "Unasoma nini?",
            listOf("j'étudie", "je fais")),
        T("Ça a l'air intéressant ! Tu as des passe-temps ?", "That sounds interesting! Do you have any hobbies?", "Inaonekana kuvutia! Una vitu unavyopenda kufanya?", "Inaonekana poa! Una vitu unapenda kufanya?",
            listOf("j'aime", "je joue")),
        T("Moi aussi j'adore ça ! On devrait échanger nos numéros.", "I love that too! We should exchange numbers.", "Mimi pia napenda hiyo! Tubadilishane nambari.", "Mimi pia napenda hiyo! Tubadilishane namba.",
            listOf("d'accord", "bonne idée"), word = "échanger = to exchange"),
        T("Super. On se reparle bientôt alors !", "Great. We'll talk again soon then!", "Vizuri. Tutaongea tena hivi karibuni basi!", "Poa. Tutaongea tena soon basi!",
            listOf("oui", "à bientôt"))
    ),
        intermediate = listOf(
            T("Tiens, on s'est déjà croisés à la soirée de la fac, non ?", "Hey, we already ran into each other at the campus party, right?", "Hé, tulishakutana kwenye sherehe ya chuo, sivyo?", "Hé, tulishakutana kwa party ya campo, sivyo?",
                listOf("oui", "je ne m'en souviens plus")),
            T("Si si, tu parlais avec le prof de français près du buffet !", "Yes yes, you were talking with the French teacher by the buffet!", "Ndiyo, ulikuwa unaongea na profesa wa Kifaransa karibu na meza ya chakula!", "Ndiyo, ulikuwa unaongea na prof wa French karibu na meza ya food!",
                listOf("ah oui", "c'est possible")),
            T("D'ailleurs, tu as fini le projet dont tu me parlais ce soir-là ?", "By the way, did you finish the project you told me about that night?", "Kwa hivyo, ulimaliza mradi ulioniambia usiku huo?", "Kwa hivyo, ulimaliza project ulioniambia that night?",
                listOf("oui", "presque", "non")),
            T("Bravo en tout cas ! On va fêter ça quand tu veux.", "Congrats anyway! We'll celebrate whenever you like.", "Hongera kwa vyovyote! Tutasherehekea unapotaka.", "Hongera anyway! Tutasherehekea unapotaka.",
                listOf("avec plaisir", "cette semaine")),
            T("Parfait, je t'enverrai l'adresse. À très vite !", "Perfect, I'll send you the address. See you very soon!", "Vizuri, nitakutumia anwani. Tutaonana hivi karibuni!", "Poa, nitakutumia address. Tuonane soon!",
                listOf("à bientôt", "super"))
        )
    ),
    Script(Scenario.JOB, listOf(
        T("Bonjour et merci d'être venu. Parlez-moi un peu de vous.", "Hello, thank you for coming. Tell me a bit about yourself.", "Habari, asante kwa kuja. Niambie kidogo kuhusu wewe.", "Mambo, asante kwa kuja. Niambie kidogo kuhusu wewe.",
            listOf("je suis étudiant", "je m'appelle"), word = "parler de = to talk about"),
        T("Très bien. Pourquoi voulez-vous ce stage ?", "Very good. Why do you want this internship?", "Vizuri sana. Kwa nini unataka mafunzo haya?", "Poa sana. Kwa nini unataka hii internship?",
            listOf("je veux", "j'aime")),
        T("Quelles sont vos compétences principales ?", "What are your main skills?", "Ujuzi wako mkuu ni upi?", "Skills zako kuu ni zipi?",
            listOf("je sais", "je peux")),
        T("Avez-vous déjà travaillé en équipe ?", "Have you worked in a team before?", "Umeshafanya kazi na timu?", "Umeshafanya kazi na team?",
            listOf("oui", "j'ai travaillé")),
        T("Avez-vous des questions pour moi ?", "Do you have any questions for me?", "Una maswali kwangu?", "Una maswali kwangu?",
            listOf("oui", "non merci")),
        T("Merci pour cet entretien. Nous vous contacterons bientôt !", "Thank you for this interview. We'll contact you soon!", "Asante kwa mahojiano haya. Tutakuwasiliana hivi karibuni!", "Asante kwa hii interview. Tutakuwasiliana soon!",
            listOf("merci", "au revoir"))
    ),
        intermediate = listOf(
            T("J'ai lu votre CV avec intérêt — vous avez déjà fait un stage l'année dernière ?", "I read your CV with interest — you already did an internship last year?", "Nimesoma CV yako kwa hamu — ulishafanya mafunzo mwaka uliopita?", "Nimesoma CV yako na interest — ulishafanya internship last year?",
                listOf("oui", "non, c'est mon premier"), word = "un CV = a résumé"),
            T("Et qu'est-ce que ce stage vous a appris, concrètement ?", "And what did that internship concretely teach you?", "Na mafunzo hayo yalikufundisha nini hasa?", "Na hiyo internship ilikufundisha nini exactly?",
                listOf("j'ai appris à", "beaucoup de choses")),
            T("Bien. Et où vous voyez-vous dans trois ans ?", "Good. And where do you see yourself in three years?", "Vizuri. Na unajiona wapi baada ya miaka mitatu?", "Poa. Na unajiona wapi after miaka mitatu?",
                listOf("je serai ingénieur", "je ferai un master")),
            T("Dernière question : pourquoi vous, et pas un autre candidat ?", "Last question: why you, and not another candidate?", "Swali la mwisho: kwa nini wewe, na si mgombea mwingine?", "Swali la mwisho: kwa nini wewe, na si candidate mwingine?",
                listOf("je travaille dur", "je suis motivé")),
            T("Merci, c'est tout pour moi. On vous écrira cette semaine !", "Thank you, that's all from me. We'll write to you this week!", "Asante, hiyo ndiyo yote kwangu. Tutakuandikia wiki hii!", "Asante, hiyo ndiyo yote kwangu. Tutakuandikia hii week!",
                listOf("merci beaucoup", "au revoir"))
        )
    ),
    Script(Scenario.BANK,
        beginner = listOf(
            T("Bonjour ! Bienvenue à la banque. Comment puis-je vous aider ?", "Hello! Welcome to the bank. How can I help you?", "Habari! Karibu benki. Nawezaje kukusaidia?", "Mambo! Karibu bank. Nawezaje kukusaidia?",
                listOf("je voudrais retirer", "bonjour")),
            T("Très bien. Combien voulez-vous retirer ?", "Very good. How much do you want to withdraw?", "Vizuri. Unataka kutoa kiasi gani?", "Poa. Unataka kutoa ngapi?",
                listOf("je voudrais", "cent francs", "mille francs")),
            T("Et les frais sont petits aujourd'hui. Vous payez en espèces ou par mobile ?", "And fees are small today. Cash or mobile?", "Na makato ni madogo leo. Taslimu au simu?", "Na charges ni small leo. Cash au simu?",
                listOf("par mobile", "en espèces"), word = "les frais = fees"),
            T("Parfait ! Voici votre reçu. Bonne journée !", "Perfect! Here's your receipt. Have a good day!", "Vizuri! Hii hapa risiti yako. Siku njema!", "Poa! Hii hapa risiti yako. Day njema!",
                listOf("merci", "au revoir"))
        ),
        intermediate = listOf(
            T("Bonjour ! Vous êtes venu retirer ou déposer de l'argent ?", "Hello! Did you come to withdraw or deposit money?", "Habari! Umekuja kutoa au kuweka pesa?", "Mambo! Umekuja kutoa ama kuweka doh?",
                listOf("je suis venu retirer", "je vais déposer"), word = "retirer/déposer = withdraw/deposit"),
            T("D'accord. Vous avez déjà rempli le formulaire ?", "Okay. Have you already filled in the form?", "Sawa. Umeshajaza fomu?", "Sawa. Umeshajaza form?",
                listOf("oui", "non, pas encore")),
            T("Pas de souci, on va le remplir ensemble. Il vous faudra aussi une pièce d'identité.", "No worries, we'll fill it in together. You'll also need an ID.", "Hamna shida, tutajaza pamoja. Utahitaji pia kitambulisho.", "Hamna shida, tutajaza pamoja. Utahitaji ID pia.",
                listOf("d'accord", "merci beaucoup")),
            T("Voilà, c'est terminé ! Vous recevrez un SMS de confirmation.", "There, all done! You'll get a confirmation SMS.", "Hapo, imemalizika! Utapokea SMS ya uthibitisho.", "Hapo, imemalizika! Utapata SMS ya confirmation.",
                listOf("merci", "parfait"))
        )
    ),
    Script(Scenario.DOCTOR,
        beginner = listOf(
            T("Bonjour, qu'est-ce qui ne va pas ?", "Hello, what's wrong?", "Habari, shida ni nini?", "Mambo, shida ni nini?",
                listOf("je suis malade", "j'ai mal")),
            T("Depuis quand êtes-vous malade ?", "Since when have you been sick?", "Umekuwa mgonjwa tangu lini?", "Umekuwa mgonjwa tangu lini?",
                listOf("depuis hier", "depuis deux jours")),
            T("Vous avez de la fièvre ?", "Do you have a fever?", "Una homa?", "Una homa?",
                listOf("oui", "non", "un peu")),
            T("Prenez deux comprimés par jour, et reposez-vous bien.", "Take two pills a day, and rest well.", "Tumia vidonge viwili kwa siku, na pumzika vizuri.", "Tumia vidonge viwili kwa siku, na pumzika poa.",
                listOf("merci", "d'accord"), word = "un comprimé = a pill"),
            T("Revenez me voir dans trois jours. Bon rétablissement !", "Come back in three days. Get well soon!", "Rudi baada ya siku tatu. Pona haraka!", "Rudi after siku tatu. Pona haraka!",
                listOf("merci", "au revoir"))
        ),
        intermediate = listOf(
            T("Bonjour, racontez-moi ce qui vous amène.", "Hello, tell me what brings you in.", "Habari, niambie kilichokuleta.", "Mambo, niambie shida ni nini.",
                listOf("j'ai mal à", "je tousse", "j'ai de la fièvre")),
            T("Et vous avez déjà pris quelque chose pour ça ?", "And have you already taken anything for it?", "Na umeshatumia dawa yoyote?", "Na umeshatumia dawa yoyote?",
                listOf("non", "oui, du paracétamol")),
            T("Je vais vous prescrire quelque chose de simple. Évitez le froid et buvez beaucoup d'eau.", "I'll prescribe something simple. Avoid the cold and drink lots of water.", "Nitakuandikia dawa rahisi. Epuka baridi na kunywa maji mengi.", "Nitakuandikia dawa rahisi. Epuka baridi na kunywa maji mob.",
                listOf("merci docteur", "d'accord")),
            T("Si ça ne va pas mieux dans trois jours, revenez me voir.", "If it's not better in three days, come back.", "Kama hautapona baada ya siku tatu, rudi.", "Kama hautapona after siku tatu, rudi.",
                listOf("merci", "au revoir"))
        )
    ),
    Script(Scenario.RENT,
        beginner = listOf(
            T("Bonjour ! Vous cherchez une chambre ?", "Hello! Are you looking for a room?", "Habari! Unatafuta chumba?", "Mambo! Unasaka keja?",
                listOf("oui", "je cherche une chambre"), word = "une chambre = a room"),
            T("Quel est votre budget par mois ?", "What's your monthly budget?", "Bajeti yako kwa mwezi ni kiasi gani?", "Budget yako kwa mwezi ni ngapi?",
                listOf("dix mille", "quinze mille", "vingt mille")),
            T("J'ai une belle chambre, meublée, avec de l'eau chaude.", "I have a lovely furnished room with hot water.", "Nina chumba kizuri, chenye samani na maji ya moto.", "Niko na keja poa, na samani na maji moto.",
                listOf("je veux visiter", "c'est combien")),
            T("Voulez-vous la visiter demain ?", "Do you want to see it tomorrow?", "Unataka kukitembelea kesho?", "Unataka kukiona kesho?",
                listOf("oui", "d'accord", "à quelle heure")),
            T("Parfait, à demain alors !", "Perfect, see you tomorrow then!", "Vizuri, tutaonana kesho!", "Poa, tuonane kesho!",
                listOf("merci", "à demain"))
        ),
        intermediate = listOf(
            T("Bonjour ! Vous cherchez un logement dans quel quartier ?", "Hello! Which neighbourhood are you looking in?", "Habari! Unatafuta nyumba mtaa gani?", "Mambo! Unasaka keja mtaa gani?",
                listOf("je cherche près de", "n'importe où")),
            T("Les loyers ont augmenté cette année. Quel est votre budget maximum ?", "Rents went up this year. What's your maximum budget?", "Kodi zimepanda mwaka huu. Bajeti yako ya juu ni kiasi gani?", "Rent imepanda hii year. Budget yako ya juu ni ngapi?",
                listOf("mon budget est de", "je peux payer")),
            T("Pour ce prix, j'ai un studio meublé avec gardien et parking.", "For that price, I have a furnished studio with a guard and parking.", "Kwa bei hiyo, nina studio yenye samani, mlinzi na maegesho.", "Kwa hiyo bei, niko na bedsitter na samani, watchman na parking.",
                listOf("je veux le voir", "c'est trop cher")),
            T("On signe le bail quand vous voulez. La caution, c'est un mois.", "We sign the lease whenever you like. Deposit is one month.", "Tutasaini mkataba unapotaka. Dipoziti ni mwezi mmoja.", "Tutasign mkataba unapotaka. Deposit ni mwezi mmoja.",
                listOf("d'accord", "je réfléchis"))
        )
    ),
    Script(Scenario.BARBER,
        beginner = listOf(
            T("Salut ! Assieds-toi. Quelle coupe tu veux ?", "Hi! Sit down. What haircut do you want?", "Niaje! Keti. Unataka kukatwa vipi?", "Niaje! Keti. Unataka kukatwa aje?",
                listOf("court", "comme d'habitude", "un peu"), word = "une coupe = a haircut"),
            T("Court sur les côtés ?", "Short on the sides?", "Fupi kando?", "Fupi kwa sides?",
                listOf("oui", "non", "un peu")),
            T("Et dessus, on garde la longueur ?", "And on top, we keep the length?", "Na juu, tunaweka urefu?", "Na juu, tunaweka length?",
                listOf("oui", "coupez un peu")),
            T("Regarde dans le miroir, ça te plaît ?", "Look in the mirror, do you like it?", "Angalia kwa kioo, unapenda?", "Angalia kwa kio, unapenda?",
                listOf("oui", "c'est parfait", "un peu plus")),
            T("C'est combien ? … Allez, pour toi, prix d'ami !", "How much? … Come on, friend's price for you!", "Ni ngapi? … Sawa, kwa ajili yako, bei ya kirafiki!", "Ni how much? … Sawa, kwa ajili yako, bei ya beshte!",
                listOf("merci", "combien"))
        ),
        intermediate = listOf(
            T("Salut champion ! On fait quoi aujourd'hui, la même que d'habitude ?", "Hey champ! What are we doing today, the usual?", "Niaje mshindi! Tunafanya nini leo, kama kawaida?", "Niaje champ! Tunafanya nini leo, kama kawa?",
                listOf("oui, comme d'habitude", "non, changeons")),
            T("Tu veux que je lave avant de couper ?", "Do you want me to wash before cutting?", "Unataka nioshe kabla sijakata?", "Unataka nioshe kabla sijakata?",
                listOf("oui s'il vous plaît", "non merci")),
            T("J'ai vu le match hier soir — ton équipe a encore perdu !", "I saw the match last night — your team lost again!", "Niliona mechi jana usiku — timu yako ilishindwa tena!", "Niliona game jana usiku — team yako ilishindwa tena!",
                listOf("pas vrai", "la prochaine fois", "on verra"),
                branches = listOf(
                    Branch(listOf("pas vrai", "non"),
                        T("Si si, deux à zéro ! Tu dormais ou quoi ? 😄", "Yes yes, two-nil! Were you asleep or what? 😄", "Ndiyo, mbili sifuri! Ulikuwa umelala? 😄", "Ndiyo, mbili bila! Ulikuwa umelala ama? 😄"))
                )),
            T("Et voilà le travail ! Tu reviens quand ?", "And there's the work! When are you coming back?", "Na kazi imekwisha! Unarudi lini?", "Na kazi imeisha! Unarudi lini?",
                listOf("la semaine prochaine", "dans deux semaines"))
        )
    ),
    Script(Scenario.DERBY,
        beginner = listOf(
            T("Mon équipe est la meilleure, c'est évident !", "My team is the best, obviously!", "Timu yangu ni bora, ni wazi!", "Team yangu ni kali, ni wazi!",
                listOf("pas vrai", "je ne suis pas d'accord", "si")),
            T("Mais on a gagné trois fois cette saison !", "But we won three times this season!", "Lakini tumeshinda mara tatu msimu huu!", "Lakini tumeshinda mara tatu hii season!",
                listOf("la chance", "pas vraiment")),
            T("Bon, tu as peut-être raison… cette fois.", "Okay, you may be right… this time.", "Sawa, labda uko sahihi… safari hii.", "Sawa, labda uko right… hii time.",
                listOf("on est amis", "revanche")),
            T("Revanche la semaine prochaine ? Que le meilleur gagne !", "Rematch next week? May the best win!", "Marudiano wiki ijayo? Bora ashinde!", "Rematch wiki ijayo? Bora ashinde!",
                listOf("d'accord", "tu vas perdre"))
        ),
        intermediate = listOf(
            T("Franchement, ton équipe n'a aucune défense cette saison.", "Honestly, your team has no defence this season.", "Ukweli, timu yako haina ulinzi msimu huu.", "Ukweli, team yako haina defence hii season.",
                listOf("pas d'accord", "c'est faux", "tu exagères"), word = "franchement = honestly"),
            T("Regarde les statistiques avant de parler !", "Look at the stats before talking!", "Angalia takwimu kabla ya kuongea!", "Angalia stats kabla ya kuongea!",
                listOf("les chiffres mentent", "d'accord")),
            T("On parie ? Le perdant paie les sodas !", "Wanna bet? Loser buys the sodas!", "Tuweke dau? Aliyeshindwa anunue soda!", "Tuweke bet? Aliyeshindwa anunue soda!",
                listOf("pari tenu", "d'accord", "non merci")),
            T("Sans rancune, on reste amis — mais mon équipe reste la meilleure !", "No hard feelings, we're still friends — but my team is still the best!", "Bila chuki, bado marafiki — lakini timu yangu bado ni bora!", "Bila noma, bado mabeshte — lakini team yangu bado ni kali!",
                listOf("on verra", "à la prochaine"))
        )
    ),
    Script(Scenario.SORRY,
        beginner = listOf(
            T("Tu es en retard ! Je t'attends depuis une heure.", "You're late! I've been waiting an hour.", "Umechelewa! Nimekungoja kwa saa moja.", "Umechelewa! Nimekungoja for saa moja.",
                listOf("pardon", "je suis désolé")),
            T("Et tu as oublié mon anniversaire aussi ?", "And you forgot my birthday too?", "Na ulisahau siku yangu ya kuzaliwa pia?", "Na ulisahau birthday yangu pia?",
                listOf("pardon", "toutes mes excuses")),
            T("Bon… qu'est-ce que tu proposes pour te faire pardonner ?", "Well… what do you suggest to make up for it?", "Sawa… unafikiri ufanye nini ili nisamehe?", "Sawa… unafikiri ufanye nini nisamehe?",
                listOf("un café", "je t'invite", "pardon")),
            T("D'accord, je te pardonne. Mais ne recommence pas !", "Okay, I forgive you. But don't do it again!", "Sawa, nimekusamehe. Lakini usirudie!", "Sawa, nimekusamehe. Lakini usirudie!",
                listOf("promis", "merci"))
        ),
        intermediate = listOf(
            T("Tu es arrivé avec une heure de retard hier, sans même prévenir.", "You arrived an hour late yesterday without even warning.", "Ulifika na saa moja kuchelewa jana bila hata kuniambia.", "Ulifika na saa moja late jana bila hata kuniambia.",
                listOf("je suis vraiment désolé", "pardon")),
            T("J'avais préparé quelque chose de spécial pour toi.", "I had prepared something special for you.", "Nilikuwa nimekuandalia kitu maalum.", "Nilikuwa nimekuandalia kitu special.",
                listOf("je ne savais pas", "pardon")),
            T("Bon. Pour te faire pardonner, tu m'invites au restaurant samedi.", "Fine. To make up for it, you're taking me to a restaurant Saturday.", "Sawa. Ili nisamehe, unanialika mgahawani Jumamosi.", "Sawa. Ili nisamehe, unanialika restaurant Jumamosi.",
                listOf("d'accord", "avec plaisir")),
            T("C'est noté. Je te pardonne — on n'en parle plus !", "Noted. I forgive you — let's not speak of it again!", "Sawa. Nimekusamehe — tusizungumzie tena!", "Sawa. Nimekusamehe — tusizungumzie tena!",
                listOf("merci", "promis"))
        )
    ))

fun scriptFor(s: Scenario): Script = SCRIPTS.first { it.scenario == s }

/** Active turn track for a scenario + register (intermediate falls back to beginner where unwritten). */
fun trackFor(s: Scenario, r: Reg): List<Turn> {
    val script = scriptFor(s)
    return if (r == Reg.INTERMEDIAIRE && script.intermediate.isNotEmpty()) script.intermediate else script.beginner
}

/** Gentle, respectful roasts: food, football and effort only — always landing with love. */
data class Roast(val fr: String, val en: String, val sw: String)

val ROASTS: List<Roast> = listOf(
    Roast("Ton accent est comme le pilau sans épices — ça se mange, mais on sent qu'il manque quelque chose 😄", "Your accent is like pilau with no spices — edible, but something's missing 😄", "Lafudhi yako ni kama pilau bila viungo 😄"),
    Roast("Tu parles français comme un matatu sans freins : vite, fort, et on ne sait jamais où ça s'arrête 😄", "You speak French like a matatu with no brakes: fast, loud, destination unknown 😄", "Unaongea Kifaransa kama matatu bila breki 😄"),
    Roast("Même mon petit cousin de cinq ans conjugue plus vite… mais il ne sait pas commander le nyama choma comme toi 😌", "Even my five-year-old cousin conjugates faster… but he can't order nyama choma like you 😌", "Hata mtoto mdogo ana-conjugate haraka… lakini hawezi kuagiza nyama choma kama wewe 😌"),
    Roast("Tête de linotte ! Heureusement que les linottes sont mignonnes 😊", "Birdbrain! Luckily linnets are cute 😊", "Kichwa cha ndege! Kwa bahati ndege ni wazuri 😊"),
    Roast("Espèce de chamallow ! Doux, sucré, et légèrement grillé par mes vannes 😄", "You marshmallow! Sweet, soft, lightly roasted by my banter 😄", "Wewe ni laini kama marshmallow 😄"),
    Roast("Tu cours après le subjonctif comme un policier après un matatu en surcharge — courage ! 😅", "You chase the subjunctive like traffic police chase an overloaded matatu — courage! 😅", "Unafukuzia subjonctif kama karao wanavyofukuzia matatu 😅"),
    Roast("Cornichon ! C'est un compliment, les cornichons sont délicieux 🥒", "You pickle! That's a compliment, pickles are delicious 🥒", "Wewe ni kama tangawizi — tamu! 🥒"),
    Roast("Champion du monde de la sieste, mais ton français se réveille doucement 🏆😴", "World napping champion, but your French is slowly waking up 🏆😴", "Bingwa wa usingizi, lakini Kifaransa chako kinaamka 🏆😴"),
    Roast("Roi de la dernière minute ! Même tes verbes arrivent en retard, mais ils arrivent 👑", "King of last-minute! Even your verbs arrive late, but they arrive 👑", "Mfalme wa dakika za mwisho! Hata vitenzi vyako vinachelewa 👑"),
    Roast("On t'aime quand même — surtout quand tu sors un 'bonjour' parfait de nulle part ❤️", "We love you anyway — especially when a perfect 'bonjour' comes out of nowhere ❤️", "Tunakupenda hivyo hivyo — hasa ukitoa 'bonjour' safi ❤️")
)

/** How well the learner's free text matches any of a turn's expected French replies. */
fun bestMatch(text: String, expects: List<String>): Double =
    if (expects.isEmpty()) 1.0 else expects.maxOf { similarity(text, it) }

/**
 * Simba's personality everywhere: a gentle post-session vanne (roast) that
 * always lands with love — food, football and effort only, never the person.
 * Returns (french, help-in-[lang]) for direct use in chat/call/finish cards.
 */
fun simbaVanne(lang: HelpLang): Pair<String, String> {
    val r = ROASTS.random()
    val help = when (lang) {
        HelpLang.ENGLISH -> r.en
        HelpLang.SWAHILI -> r.sw
        HelpLang.SHENG -> r.sw
    }
    return ("😈 " + r.fr) to help
}
