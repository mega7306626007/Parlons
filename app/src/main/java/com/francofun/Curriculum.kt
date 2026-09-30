package com.francofun

// Curriculum layer: how real curriculums work — every unit gets a TOPIC
// overview (what + why), READING NOTES (Kenyan-bridged explanations),
// OBJECTIVES ("by the end you can…") and a QUIZ. Additive: new guides
// append to UNIT_GUIDES; nothing here edits lesson content.

data class NoteSection(val title: String, val body: String)

data class QuizQ(
    val q: String,
    val options: List<String>,
    val answer: Int, // index into options
    val explain: String
)

data class UnitGuide(
    val unitId: String,
    val topicFr: String,
    val topicEn: String,
    val topicSw: String,
    val objectives: List<String>,
    val notes: List<NoteSection>,
    val quiz: List<QuizQ>
)

fun guideFor(unitId: String): UnitGuide? = UNIT_GUIDES.find { it.unitId == unitId }

val UNIT_GUIDES: List<UnitGuide> = listOf(
    UnitGuide(
        "u1", "Survie", "Survival French (A1)", "Kuanza (A1)",
        objectives = listOf(
            "Greet anyone, any time of day, with the right title (madame/monsieur)",
            "Introduce yourself: name, age (with avoir!), home, school",
            "Count 0–100 and spell your name aloud for forms and phone calls"
        ),
        notes = listOf(
            NoteSection(
                "Greetings carry titles — the full system",
                "French greetings have two layers: the TIME word plus the TITLE. Time: Bonjour (until ~6pm) → Bonsoir (evening) → Bonne nuit (only when someone sleeps). Title: + madame / monsieur to any stranger, shopkeeper, official, elder. « Bonjour madame » is one breath, not two words.\n\nFull ladder:\n• Strangers/officials: Bonjour madame / Bonjour monsieur (+ handshake or nod)\n• Colleagues you know: Bonjour + first name\n• Friends: Salut ! / Coucou ! / Ça va ? (no title, ever)\n• Leaving: Au revoir (+ title again) / Salut (friends) / Bonne journée !\n\n⚠️ Common error: « Bonjour » to a shopkeeper with no title — read as cold/rude, like skipping « shikamoo » for an elder. 🧠 Trick: title = respect battery; spend it on everyone you don't know. 📝 Exam link: dialogues open with greetings — the title earns politeness marks."
            ),
            NoteSection(
                "Age uses AVOIR — plus the whole avoir family",
                "« J'ai 17 ans » = « I have 17 years ». French treats age as something you POSSESS. Saying « je suis 17 ans » is the #1 Kenyan anglophone error — examiners circle it instantly.\n\nThe avoir-states family (all take avoir, never être):\n• j'ai faim / soif — hunger / thirst\n• j'ai chaud / froid — hot / cold (weather-feelings too: il fait chaud)\n• j'ai peur / honte — fear / shame\n• j'ai raison / tort — right / wrong\n• j'ai besoin de / envie de — need / want\n• j'ai sommeil — sleepy\n\nContrast with être-states: je suis fatigué, content, triste, malade, prêt, marié. 🧠 Trick: body NEEDS = avoir (hunger, thirst, cold); IDENTITY/STATE = être (tired, happy, sick). 📝 Exam link: « dis-moi ton âge » appears in every oral — answer « J'ai… ans » without thinking."
            ),
            NoteSection(
                "The alphabet is a survival skill — letter by letter",
                "French letter names differ from English ones and from each other in sound-alikes: B/D, C/S, G/J, M/N, U/Q. Phone calls, hotel check-ins, bank forms, hospital cards — « Je m'appelle Wanjiru : W-A-N-J-I-R-U » — this is real-life French from week one.\n\nDanger pairs to drill aloud:\n• B [be] vs D [de] vs T [te]\n• C [se] vs S [esse] vs X [ixe]\n• G [ʒe] vs J [ʒi]\n• U [y] vs Q [ky] vs OU (not a letter!)\n• H [aʃe] — always spelled, never sounded (hôpital, hôtel)\n\nWorked example: « Mon nom : Njoroge — N-J-O-R-O-G-E : enne-ji-o-erre-o-jé-euh. » 🧠 Trick: learn the 8 danger letters first (B C D G J S U X); the rest resemble English. 📝 Exam link: dictation (Paper 1) tests exactly these confusions — B/D and S/C swaps."
            ),
            NoteSection(
                "Questions: three gears, one engine",
                "Every French question exists in 3 registers. Master all three and switch by company:\n\n1. INTONATION (friends, speech): statement + rising voice. « Tu viens ? » « T'as mangé ? » Fastest, most used.\n2. EST-CE QUE (neutral, everywhere): « Est-ce que tu viens ? » The exam-safe default. Works with every question word: Où est-ce que… ? Quand est-ce que… ?\n3. INVERSION (formal, writing): « Viens-tu ? » « Où habitez-vous ? » Add -t- between vowels: « Va-t-il pleuvoir ? »\n\nQuestion words: qui (who), quoi/qu'est-ce que (what), où (where — accent!), quand (when), comment (how), pourquoi (why → answer parce que), combien (how much: + de + noun → « Combien de frères as-tu ? »), quel (which — agrees: quelle/ quels/quelles).\n\n⚠️ Common errors: « ou » (or) vs « où » (where) — the accent is the whole meaning. « Si » never takes the future. 🧠 Trick: one gear per audience — friends: gear 1, exams: gear 2, letters: gear 3."
            )
        ),
        quiz = listOf(
            QuizQ("It's 7pm. You meet a stranger. You say…", listOf("Bonjour", "Bonsoir", "Salut mec"), 1, "After ~6pm, Bonjour becomes Bonsoir. Salut is friends-only."),
            QuizQ("« J'ai 16 ans » means…", listOf("I am 16", "I have 16 (years)", "Both — same thing"), 1, "Age uses avoir: literally « I have 16 years »."),
            QuizQ("Which is correct?", listOf("Je suis faim", "J'ai faim", "Je fais faim"), 1, "Hunger takes avoir: j'ai faim, j'ai soif, j'ai peur."),
            QuizQ("Spell your name on the phone. You need…", listOf("The alphabet lesson", "Numbers 0–100", "Colours"), 0, "Letter names differ from English — the alphabet lesson is the tool."),
            QuizQ("« Est-ce que tu viens ? » is…", listOf("Rude", "The neutral standard question", "Formal only"), 1, "est-ce que works in every register — the safe default."),
            QuizQ("Greet a female teacher respectfully:", listOf("Salut !", "Bonjour madame", "Coucou !"), 1, "Strangers + title. Always."),
            QuizQ("« Comment ça va ? » expects…", listOf("Silence", "« Ça va » + return question", "Your life story"), 1, "Ça va bien, et toi ? Small talk is a rally, not a speech."),
            QuizQ("Count: quatre-vingt-dix means…", listOf("80", "90", "100"), 1, "80 = quatre-vingts (four-twenties), 90 = quatre-vingt-dix (four-twenty-ten).")
        )
    ),
    UnitGuide(
        "u2", "Le quotidien", "Daily life (A1–A2)", "Maisha ya kila siku",
        objectives = listOf(
            "Shop and bargain: prices, sizes, « c'est trop cher »",
            "Talk family, feelings, weather, school and work",
            "Tell time in French (midnight-based, unlike Swahili time)"
        ),
        notes = listOf(
            NoteSection(
                "Swahili time vs French time — the conversion system",
                "Two clocks run in your head. SWAHILI time starts at 6am (sunrise): 7am = saa moja, 8am = saa mbili, 1pm = saa saba, 7pm = saa moja jioni. FRENCH time starts at midnight like English: 8am = huit heures, 1pm = treize heures (or une heure de l'après-midi), 7pm = dix-neuf heures (or sept heures du soir).\n\nConversion rules:\n• Morning (6am–12pm): French = Swahili + 6. Saa mbili → huit heures.\n• Afternoon: French 24h = Swahili + 6 (saa saba → treize heures); or 12h + « de l'après-midi » (une heure de l'après-midi).\n• Evening/night: « du soir » / « du matin » disambiguate: huit heures du soir (8pm) vs huit heures du matin (8am).\n• Half/quarter: huit heures et demie (8:30), neuf heures moins le quart (8:45), dix heures et quart (10:15).\n\nWorked example: classes end « saa kumi na moja alfajiri » (5am) → « cinq heures du matin ». A matatu leaves « saa nne asubuhi » (10am) → « dix heures ».\n\n⚠️ Common error: translating saa directly (« *deux heures » for 8am). Always +6 first. 🧠 Trick: French hour = Swahili number + 6, morning only; afternoon switch to 24h. 📝 Exam link: time conversion is tested almost yearly — « Il est quelle heure ? » with a Swahili-time context."
            ),
            NoteSection(
                "Bargaining is a dialogue — the 6-move script",
                "Markets from Gikomba to Marché Bastille run the same game. Learn MOVES, not words:\n\n1. GREET + ASK: « Bonjour madame ! C'est combien, ça ? » (smile, touch nothing yet)\n2. REACT: wince — « Ooh… c'est trop cher ! » (never accept first price)\n3. COUNTER: offer ~60%: « Je vous propose deux cents. » (state it warmly)\n4. THEATRE: hesitate, walk two steps — « Bon, je vais réfléchir… » (vendor calls back)\n5. MEET HALFWAY: « On se retrouve au milieu ? » / « Faites-moi un petit prix ! »\n6. CLOSE: « Marché conclu ! Merci beaucoup, à la prochaine ! »\n\nKey lines: « C'est mon dernier prix » (vendor's floor), « Je n'ai que… » (your ceiling), « Pour vous, spécialement… » (flattery = discount).\n\n⚠️ Common error: opening with the price instead of greeting — no rapport, no discount. 🧠 Trick: the first price is fiction; the real price lives at 60–70%. 📝 Exam link: market dialogues are a KCSE staple — greet, react, counter, close."
            ),
            NoteSection(
                "Feelings: the avoir list vs the être list",
                "French splits feelings across two verbs with ZERO overlap. Memorize both lists whole:\n\nAVOIR (body needs & states): faim (hunger), soif (thirst), chaud/froid (hot/cold), peur (fear), honte (shame), raison/tort (right/wrong), besoin de (need), envie de (want), sommeil (sleepiness), mal (pain: j'ai mal à la tête).\n\nÊTRE (identity & condition): fatigué, content/triste, malade, prêt, marié/célibataire, en retard/en avance, d'accord/pas d'accord.\n\nTest yourself: « I am hungry » → J'ai faim ✓ (never *je suis faim). « I am right » → J'ai raison ✓ (never *je suis raison). « She is late » → Elle est en retard ✓ (never *elle a retard).\n\n⚠️ Common error: importing English « am » everywhere (*je suis faim, *je suis 20 ans). 🧠 Trick: NEEDS you feel in your body = avoir; WHAT you are = être. 📝 Exam link: dialogues (doctor, hungry child, late friend) probe exactly this split."
            ),
            NoteSection(
                "Weather: small talk that opens doors",
                "Weather talk is social currency — in France like rain talk in Kenya. Core set:\n• Il fait beau / chaud / froid / frais / gris / lourd (muggy)\n• Il pleut / Il neige / Il y a du vent / du soleil / des nuages\n• Quel temps fait-il ? (formal) / Il fait quel temps ? (neutral)\n\nSeasons: le printemps (spring), l'été (summer), l'automne (autumn), l'hiver (winter). Kenyan bridge: « En juillet il fait frais à Limuru » / « En mars il pleut à Nairobi » — same pattern, home content. Follow-ups that keep conversation alive: « Et demain ? » / « Tu préfères quelle saison ? » / « Il fait combien aujourd'hui ? »\n\n⚠️ Common error: « *Il est chaud » for weather (that means HE is hot!). Weather = il fait + adjective, always. 🧠 Trick: il fait = it makes (the weather); il est = he/it is (a person/thing). 📝 Exam link: « décrivez le temps » + seasons appear in comprehension and orals."
            )
        ),
        quiz = listOf(
            QuizQ("Swahili « saa mbili asubuhi » = French…", listOf("deux heures", "huit heures", "six heures"), 1, "Add 6: Swahili 2 → French 8. French counts from midnight."),
            QuizQ("Vendor says 500. You bargain:", listOf("Pay silently", "« C'est trop cher ! »", "Walk away angry"), 1, "C'est trop cher + Faites-moi un petit prix. Play the game."),
            QuizQ("« J'ai froid » uses avoir because…", listOf("Cold is a possession", "States of body take avoir", "It's irregular"), 1, "Body states (faim, soif, chaud, froid, peur) all take avoir."),
            QuizQ("« Il fait gris » means…", listOf("It's grey/overcast", "It's great", "It's freezing"), 0, "gris = grey sky. Weather adjectives: beau, chaud, froid, gris, frais."),
            QuizQ("Your sister is…", listOf("mon frère", "ma sœur", "mon oncle"), 1, "sœur (f) → ma. frère (m) → mon. Noun gender rules."),
            QuizQ("« Je suis d'accord » means…", listOf("I disagree", "I agree", "I'm tired"), 1, "être d'accord = agree. Disagree: Je ne suis pas d'accord parce que…"),
            QuizQ("School subject maths in French:", listOf("les maths (plural)", "la math", "le math"), 0, "les mathématiques, les sciences — school subjects love plurals."),
            QuizQ("« À quelle heure ? » asks…", listOf("Where?", "At what time?", "How much?"), 1, "à + heure = time. D'où (where from), combien (how much).")
        )
    ),
    UnitGuide(
        "u3", "Comme un local", "Talk like a local (A2)", "Kama mwenyeji",
        objectives = listOf(
            "Run hobbies, phone, health, travel and opinion conversations",
            "Command être/avoir/aller/faire without thinking",
            "Tell past events in passé composé (avoir vs être)"
        ),
        notes = listOf(
            NoteSection(
                "Opinions: the skeleton that earns marks",
                "Examiners don't score opinions — they score STRUCTURE. The full skeleton:\n\n1. POSITION: « À mon avis… » / « Selon moi… » / « Pour ma part… »\n2. REASON: « …parce que… » / « …car… » / « …grâce à… »\n3. EXAMPLE: « Par exemple, à Kisumu… » (proper noun + detail — concrete beats abstract)\n4. CONCESSION (B1+): « Certes…, mais… » / « Il est vrai que…, pourtant… »\n\nAgreement/disagreement openers:\n• « Je suis (tout à fait) d'accord. » / « Je ne suis pas d'accord parce que… »\n• « Tu as raison, mais… » (softens the blow)\n• « C'est vrai que…, cependant… » (formal, essay-ready)\n• « Pas vraiment… » / « Pas du tout ! » (familiar, football debates)\n\nWorked example: « À mon avis, le football unit les Kenyans parce qu'en 2023 tout Nairobi portait le maillot des Harambee Stars. Certes, les tickets coûtent cher, mais la joie est gratuite. » — position, reason, example, concession. Full marks.\n\n⚠️ Common error: opinion with no parce que — a naked claim scores half. 🧠 Trick: every « à mon avis » MUST be followed by « parce que » within two sentences. 📝 Exam link: debates and compositions are graded on this skeleton."
            ),
            NoteSection(
                "Passé composé: the 80% rule + agreement traps",
                "Formation: AVOIR/ÊTRE (present) + past participle (-é/-i/-u + irregulars: fait, pris, mis, dit, écrit, vu, bu, lu).\n\nTHE rule: movement/change verbs take ÊTRE — DR MRS VANDERTRAMP: Devenir, Revenir, Mourir, Retourner, Sortir, Venir, Arriver, Naître, Descendre, Entrer, Rester, Tomber, Rentrer, Aller, Monter, Partir. EVERYTHING else takes avoir.\n\nAgreement — only 2 cases, both written-only:\n1. être-verbs agree with SUBJECT: Elle est allée, Ils sont partis.\n2. avoir-verbs agree with a PRECEDING direct object: « les mangues que j'ai mangées » — but « J'ai mangé les mangues » (object after → nothing).\n\nNegation wraps the AUXILIARY: « Je n'ai pas mangé », « Elle n'est jamais venue ».\n\nWorked chain: « Hier, je suis allé au marché (être, masc), j'ai acheté des mangues (avoir), les mangues que j'ai achetées étaient mûres (preceding object → -es). »\n\n⚠️ Common error: *j'ai allé, *elle est mangé — auxiliary swap. 🧠 Trick: ask « movement or change? » — yes → être. 📝 Exam link: agreement with preceding object is THE most-tested written trap."
            ),
            NoteSection(
                "on = we: the spoken-first pronoun",
                "In real speech, « on » has replaced « nous » ~90% of the time:\n• « On va au match ? » (= allons-nous — but nobody says that aloud)\n• « On mange à midi. » / « On verra. » / « On y va ! »\n• « On » takes IL-forms: on a, on est, on va (never *on avons).\n\nRegister map:\n• Speech/friends: on always. « On s'est bien marrés ! »\n• Formal writing/exams: nous. « Nous avons analysé… »\n• « on » also = « people/one »: « On dit que… » (they say), « On ne fume pas » (signs).\n\nAgreement quirk: « On est partis » (masc plural possible) but verb stays singular: on a (never *ont).\n\n⚠️ Common error: *on avons / *on sont — on is grammatically singular. 🧠 Trick: replace on with « il » to check the verb: il a → on a. 📝 Exam link: dialogues and orals expect on; compositions expect nous. Switching correctly = register marks."
            ),
            NoteSection(
                "Health dialogues: the exam scene, mastered",
                "Every paper recycles the doctor visit. The fixed script:\n\n1. GREET + COMPLAINT: « Bonjour docteur, j'ai mal à la tête / au dos / aux dents. » (avoir mal À + body part)\n2. DURATION: « Ça fait mal depuis trois jours. » (depuis = still sick; pendant = over; il y a = it started)\n3. SYMPTOMS: « J'ai de la fièvre, je tousse, je n'ai pas dormi. »\n4. DOCTOR: « Ouvrez la bouche. Respirez. Prenez ceci deux fois par jour. » (imperatives)\n5. CLOSE: « Merci docteur, au revoir ! »\n\nBody vocabulary core: la tête, le dos, le ventre, la gorge, les dents, le bras, la jambe, le pied, le cœur.\nPharmacy follow-up: « Je voudrais quelque chose pour la toux. » / « C'est sans ordonnance ? »\n\n⚠️ Common error: *je suis mal (calque) — pain is ALWAYS « j'ai mal à… ». 🧠 Trick: depuis (ongoing) vs pendant (finished) vs il y a (starting point) — the trio examiners rotate. 📝 Exam link: health is a guaranteed topic; this script banks it."
            )
        ),
        quiz = listOf(
            QuizQ("Past of « aller » (I went, masc.):", listOf("J'ai allé", "Je suis allé", "J'allais"), 1, "aller takes être. J'ai allé is the classic error."),
            QuizQ("« On y va ? » means…", listOf("Where are we?", "Shall we go?", "Who goes?"), 1, "on = we (spoken), y = there. On y va = let's go."),
            QuizQ("Give an opinion correctly:", listOf("« Je pense parce que… »", "« À mon avis… parce que… »", "« Moi, oui. »"), 1, "Opinion + reason + example. Skeleton carries the marks."),
            QuizQ("« J'ai mal à la tête depuis hier » — depuis means the pain…", listOf("Ended yesterday", "Continues now", "Starts tomorrow"), 1, "depuis + present = still ongoing. Pendant = finished span."),
            QuizQ("Feminine « she went »:", listOf("Elle est allé", "Elle est allée", "Elle a allée"), 1, "être-verbs agree: allée (f). Spelling shows what speech hides."),
            QuizQ("Phone plan: « forfait » means…", listOf("A party", "A (phone) plan", "A phone shop"), 1, "le forfait (plan), le crédit, la connexion. Tech French is exam-relevant."),
            QuizQ("« Je fais du foot » uses faire because…", listOf("Sports take faire", "Foot is food", "It's irregular"), 0, "faire + du/de la + sport: du foot, de la natation, du jogging."),
            QuizQ("Travel: « un aller-retour » is…", listOf("One-way", "Return ticket", "A delay"), 1, "aller simple (one-way) vs aller-retour (return). Counter French.")
        )
    ),
    UnitGuide(
        "u4", "Se débrouiller", "Getting things done (A2–B1)", "Kujitegemea",
        objectives = listOf(
            "Navigate: directions, transport, taxis, airports, hotels",
            "Handle money: banking, mobile money, prices in francs",
            "Manage emergencies: doctor, police, lost items, help"
        ),
        notes = listOf(
            NoteSection(
                "Directions: the fixed script + landmark system",
                "Asking: « Excusez-moi, pour aller à la gare ? » / « Je cherche l'hôpital. » / « C'est loin d'ici ? »\n\nAnswering moves (imperative vous-form — polite to strangers):\n• « Continuez tout droit jusqu'au rond-point. »\n• « Tournez à gauche / à droite après le pont. »\n• « Traversez le marché, c'est en face de la banque. »\n\nLandmark prepositions (the real vocabulary):\n• à côté de (next to), en face de (opposite), au bout de (at the end of)\n• entre… et… (between), derrière (behind), devant (in front of)\n• près de / loin de (near/far), à gauche/à droite de\n\nDistances: « C'est à cinq minutes à pied. » / « Prenez un taxi, c'est trop loin. » Close with thanks: « Merci, vous êtes très aimable ! »\n\n⚠️ Common error: *« Où est…? » for everything — vary with « Pour aller à…? » and « Je cherche… ». 🧠 Trick: instructions use VOUS-imperatives (Continuez, Tournez, Prenez) — drill 10 of them. 📝 Exam link: direction dialogues test imperatives + prepositions under time pressure."
            ),
            NoteSection(
                "Money French: bank + mobile money, the Kenyan edge",
                "Bank core: le compte (account), le retrait (withdrawal), le dépôt (deposit), le virement (transfer), le solde (balance), les frais (fees), le guichet (counter), le distributeur (ATM).\n\nKey verbs: retirer de l'argent, déposer, virer sur un compte, vérifier le solde, payer en espèces / par carte.\n\nMobile money (describe M-Pesa in French — genuine professional skill):\n• « Envoyez de l'argent par téléphone. »\n• « Tapez le code, confirmez avec votre PIN. »\n• « Le solde s'affiche par SMS. »\n• « Les frais sont de cinquante shillings. »\n\nCurrency bridge: le shilling, le franc CFA (XOF/XAF — 14 countries, fixed to the euro), le dollar, l'euro. « C'est combien en francs ? » / « Le taux est de… »\n\n⚠️ Common error: *« payer avec carte » — it's « payer PAR carte / EN espèces ». 🧠 Trick: par = means (par carte, par téléphone), en = material/state (en espèces, en dollars). 📝 Exam link: money dialogues combine numbers + negotiation + politeness."
            ),
            NoteSection(
                "Emergencies: short sentences that save marks (and people)",
                "No subjunctive, no elegance — present, passé composé, imperatives:\n\n• Alarm: « Au secours ! » / « À l'aide ! » / « Appelez une ambulance / la police ! »\n• Lost: « J'ai perdu mon passeport / portefeuille. » vs « Je me suis perdu(e). » (perdre = lose things; se perdre = lose yourself)\n• Stolen: « On m'a volé mon sac ! » (on-passive — spoken French)\n• Hurt: « Il est blessé ! » / « Elle s'est cassé la jambe. »\n• At the station: « Que dois-je faire ? » / « Où est le commissariat / l'ambassade ? »\n• Details officials need: « C'était hier vers vingt heures, près du marché. » (time + place)\n\n⚠️ Common error: long confused sentences — in emergencies AND exams, 3 short correct sentences beat 1 long broken one. 🧠 Trick: memorize 5 alarm lines cold (Au secours, Appelez…, J'ai perdu…, On m'a volé…, Où est…). 📝 Exam link: emergency scenes test imperatives + passé composé + time expressions."
            ),
            NoteSection(
                "Housing: the bedsitter checklist dialogue",
                "Renting rehearses questions, numbers and agreement under pressure. The checklist (ask ALL of these):\n\n1. « C'est combien le loyer (par mois) ? »\n2. « Combien de pièces ? » (rooms)\n3. « Les charges sont comprises ? » (utilities — feminine plural agreement!)\n4. « C'est meublé ou vide ? »\n5. « Il y a l'eau chaude ? Le wifi ? »\n6. « C'est loin du centre ? »\n7. « Quand est-ce que je peux visiter ? »\n\nNegotiate: « C'est un peu cher… Vous pouvez baisser un peu ? » Decide: « Je vais réfléchir. » / « D'accord, je le prends ! »\n\nKey nouns: le loyer, les charges, la caution (deposit), le bail (lease), le propriétaire, le quartier.\n\n⚠️ Common error: « *les charges sont compris » — charges is feminine plural: comprises. 🧠 Trick: every question you ask a landlord is an oral-exam question in disguise — rehearse the list aloud. 📝 Exam link: housing dialogues = question formation + agreement + numbers, all at once."
            )
        ),
        quiz = listOf(
            QuizQ("« Tournez à gauche » means…", listOf("Go straight", "Turn left", "Stop"), 1, "gauche (left), droite (right), tout droit (straight)."),
            QuizQ("« Les charges sont comprises ? » asks…", listOf("Are bills included?", "Is it furnished?", "How many rooms?"), 0, "les charges (utilities). comprises agrees (feminine plural)."),
            QuizQ("Lost wallet — first sentence:", listOf("« J'ai perdu mon portefeuille »", "« Je suis perdu »", "« Au revoir »"), 0, "perdre (lose things) vs se perdre (lose yourself). Both essential."),
            QuizQ("« Un retrait » at the bank is…", listOf("A deposit", "A withdrawal", "A loan"), 1, "retirer (withdraw), déposer (deposit), emprunter (borrow)."),
            QuizQ("Taxi: « C'est combien jusqu'à… ? »", listOf("What time?", "How much to…?", "Where is…?"), 1, "jusqu'à (up to/as far as). Fare negotiation in one line."),
            QuizQ("Hotel: « Le petit-déjeuner est inclus ? »", listOf("Is breakfast included?", "Is wifi free?", "What time checkout?"), 0, "inclus/incluse agrees. Check-in checklist: prix, petit-déj, wifi, checkout."),
            QuizQ("« Appelez un médecin ! » is…", listOf("A question", "An imperative order", "Past tense"), 1, "Imperatives: Appelez !, Aidez-moi !, Venez vite ! Emergencies run on orders."),
            QuizQ("At the pharmacy: « J'ai de la fièvre » — fièvre is…", listOf("Masculine", "Feminine", "Plural"), 1, "la fièvre, la toux, la grippe. Body nouns skew feminine — verify each.")
        )
    ),
    UnitGuide(
        "u5", "Créer des liens", "Connecting (B1)", "Kuwasiliana",
        objectives = listOf(
            "Make friends: small talk, invitations, flirting appropriately",
            "Tell your day as a story (past tenses working together)",
            "Debate lightly and apologize sincerely"
        ),
        notes = listOf(
            NoteSection(
                "Storytelling: the two-past engine, fully mapped",
                "Every story alternates two pasts. BACKGROUND (imparfait — what was going on): il faisait beau, j'avais peur, on marchait, il était huit heures. EVENTS (passé composé — what happened): je suis sorti, tout a changé, soudain il a plu.\n\nThe master sentence: « Je MARCHAIS (background) QUAND il A COMMENCÉ (event) à pleuvoir. » Quand + event punctures background. More connectors:\n• Pendant que + imparfait (two backgrounds run together): « Pendant que je mangeais, il parlait. »\n• Soudain / tout à coup / un jour + passé composé (rupture): « Soudain, tout a changé. »\n• D'habitude / chaque jour / toujours + imparfait (habit): « D'habitude, on jouait le samedi. »\n\nWorked mini-story: « Samedi, il faisait beau (bg). On jouait au foot (habit) quand l'arbitre est arrivé (event). Soudain, il a sifflé (rupture) — match terminé ! »\n\n⚠️ Common error: *« Quand je suis marché… » — background needs imparfait, always. 🧠 Trick: ask « photo or flash? » — photo (scene) = imparfait, flash (action) = passé composé. 📝 Exam link: « racontez votre journée/week-end » is guaranteed; the two-past mix is the marking scheme."
            ),
            NoteSection(
                "Polite disagreement: concede, then advance",
                "Flat contradiction (« T'as tort ! ») kills conversations and marks. The 4-step formula:\n\n1. ACKNOWLEDGE: « Je comprends… » / « C'est vrai que… » / « Tu n'as pas tort, mais… »\n2. PIVOT: « …mais… » / « …pourtant… » / « …cependant… »\n3. YOUR VIEW: « À mon avis… parce que… »\n4. INVITE: « Qu'est-ce que tu en penses ? » / « Tu vois ce que je veux dire ? »\n\nWorked debate (Gor vs AFC): « C'est vrai que Gor a gagné l'an dernier (concede), mais AFC joue mieux cette saison parce que leur défense est solide (advance). Par exemple, samedi… (proof). Et toi, tu dis quoi ? (invite). »\n\nEscalation control: « On est d'accord pour ne pas être d'accord. » — the graceful exit.\n\n⚠️ Common error: mais without concession first — reads as attack. 🧠 Trick: « oui, mais » beats « non » every time. 📝 Exam link: debate tasks grade structure (concede→advance→invite), not who is right."
            ),
            NoteSection(
                "Apologies: regret + responsibility + repair",
                "Three beats, all mandatory:\n\n1. REGRET (name it): « Je suis vraiment désolé(e). » / « Pardonnez-moi. » / « Excuse-moi, s'il te plaît. »\n2. RESPONSIBILITY (own it): « C'était ma faute. » / « J'aurais dû prévenir. » (conditionnel passé = mature regret) / « Je n'ai pas fait exprès. » (if truly accidental)\n3. REPAIR (fix it): « Je me rattraperai samedi. » / « Laisse-moi t'inviter. » / « Ça ne se reproduira plus. »\n\nWhat kills apologies: excuses (« C'est le trafic » — blame-shifting), minimization (« C'est rien »), empty sorry with no repair.\n\nWorked: late to a chama meeting — « Pardonnez-moi pour le retard (regret). J'aurais dû partir plus tôt (responsibility). Je paierai ma part double ce mois-ci (repair). »\n\n⚠️ Common error: désolé without agreement — « Je suis désolée » (speaker female). 🧠 Trick: RRR — Regret, Responsibility, Repair. Missing one = incomplete. 📝 Exam link: apology dialogues check all three beats."
            ),
            NoteSection(
                "Register control: slang placed, not sprayed",
                "Familier words are smaSekta-approved with friends, career-ending with officials. The map:\n\nFRIENDS ONLY: mec, ouf, kif-kif, se marrer, délirer, kiffer, ouais, bof, bosser, bouffer, bagnole, fric.\nANYWHERE: sympa, super, génial, rigoler (mild), truc, boulot (mild).\nNEVER (vulgar): merde, putain, con — know them to understand films, never produce them in class/exams.\n\nWorked switch: friend — « C'était ouf, on s'est bien marrés ! » vs examiner — « C'était formidable, nous avons beaucoup ri. » Same joy, different wardrobe.\n\n⚠️ Common error: verlan/slang in compositions — instant register penalty. 🧠 Trick: « examiner test » — would you say it to the principal? No → don't write it. 📝 Exam link: register appropriateness is an explicit marking criterion."
            )
        ),
        quiz = listOf(
            QuizQ("« Je marchais quand il a plu » — marchais is imparfait because…", listOf("It's long ago", "Ongoing background", "It's formal"), 1, "Background action = imparfait. Interrupting event = passé composé."),
            QuizQ("Disagree politely:", listOf("« T'as tort. »", "« Je comprends, mais… »", "Silence"), 1, "Concede, then advance. Structure beats volume."),
            QuizQ("Apologize completely:", listOf("« Désolé. »", "Regret + fault + repair", "« C'est le trafic. »"), 1, "Je suis désolé + c'était ma faute + je me rattraperai. Three beats."),
            QuizQ("« Soudain, tout a changé » — soudain triggers…", listOf("Imparfait", "Passé composé", "Futur"), 1, "soudain/tout à coup/un jour = sudden events = passé composé."),
            QuizQ("Tell your day: best opener?", listOf("« Ce matin, je me suis levé à… »", "« Jour. »", "« J'aime… »"), 0, "Day stories: morning anchor in passé composé, then chain events."),
            QuizQ("Flirting appropriately means…", listOf("Anything goes", "Compliments + respect + exit if unwelcome", "Avoid everyone"), 1, "Tu es sympa + conversation + grace. Respect is the whole lesson."),
            QuizQ("« On s'est bien marrés » — se marrer means…", listOf("To marry", "To laugh hard (familiar)", "To work"), 1, "se marrer (laugh), délirer, kiffer: familiar joy. Friends only."),
            QuizQ("Job interview: « Parlez-moi de vous » — answer in…", listOf("90 seconds: past→present→future", "One word", "Your salary demands"), 0, "Formation (past) → expérience (present) → projet (future). Three tenses, one hire.")
        )
    ),
    UnitGuide(
        "u6", "Pont vers la fluidité", "Fluency bridge (B1)", "Daraja",
        objectives = listOf(
            "Handle faster, native-paced audio without panic",
            "Run all 12 Simba scenarios at Intermédiaire register",
            "Answer without multiple-choice crutches (typing/speaking)"
        ),
        notes = listOf(
            NoteSection(
                "No training wheels: why production replaces recognition",
                "Recognition (multiple choice) feels like knowing; production (typing/speaking from scratch) IS knowing. Brain science: recall builds 3× stronger memory traces than recognition. U6 removes choice where typing/speaking covers the phrase — scores dip for a week, then overtake and never look back.\n\nHow to survive the switch:\n• Type what you HEAR first (dictation habit), then what you THINK (translation habit).\n• Speak every answer aloud before typing it — mouth memory + finger memory.\n• Wrong answers are the method: each correction writes the right form over the wrong one. Review your mistake notebook weekly.\n• Fuzzy grading accepts missing accents — « cafe » for « café » passes. Knowledge over spelling.\n\n⚠️ Common error: re-reading instead of recalling — feels productive, builds nothing. 🧠 Trick: cover-and-recall every phrase; if you can't produce it blank-page, you don't know it. 📝 Exam link: papers test production (composition, dictation, oral) — U6 trains exactly that."
            ),
            NoteSection(
                "Speed is a skill: the shadowing protocol",
                "Native pace blurs boundaries: liaisons (les‿amis), dropped « ne » (j'mange pas), glued pronouns (j'lui ai dit). The ear must retrain — shadowing does it:\n\nPROTOCOL (5 min daily):\n1. Pick a 30-second Simba audio or song hook.\n2. Play at 1×, repeat 1 beat behind — mimic melody, not just words.\n3. Same clip 3×: first for rhythm (mumble along), then words, then full voice.\n4. Weekly: raise to 1.1× speed. Monthly: new clip.\n\nStages you'll feel: week 1 — drowning; week 3 — catching nouns/verbs; week 6 — following stories; week 10 — predicting endings. The « catch content words, reconstruct grammar » strategy works mid-journey: nouns + verbs carry ~70% of meaning.\n\n⚠️ Common error: slow playback forever — comfort zone, no growth. Use 0.75× to decode, 1×+ to train. 🧠 Trick: shadow while walking — rhythm in feet transfers to rhythm in speech. 📝 Exam link: listening papers play twice at native pace; shadowers hear words, others hear soup."
            ),
            NoteSection(
                "The marathon: how Leitner targets YOUR gaps",
                "Every phrase lives in a box 1–5. Correct → box up, review later (tomorrow → 3 days → week → 2 weeks → month). Wrong → box 1, tomorrow. The marathon pulls lowest boxes first — your personal weakest links, not a generic list.\n\nReading your boxes:\n• Box 1–2: fragile — needs weekly contact. These dominate the marathon.\n• Box 3: stabilizing — fortnightly.\n• Box 4–5: owned — monthly maintenance.\n\nMarathon strategy: the night before, sleep (consolidation beats cramming); during, answer fast — first instinct is usually the stored trace; after, check WHICH boxes fed the session and drill those lessons normally.\n\n⚠️ Common error: re-studying box-5 favorites (feels good, learns nothing). 🧠 Trick: love your box-1 list — it's the only list that matters. 📝 Exam link: pre-exam week = daily mini-marathons on boxes 1–2 only."
            ),
            NoteSection(
                "B1 independence: the checklist",
                "CEFR B1 = independence. Verify yourself against the official-style can-dos:\n\n✓ Travel: book, navigate, handle problems (tickets, hotels, directions) — U4.\n✓ Stories: past events + background in order — U5 two-past engine.\n✓ Opinions: position + reason + example — U3/U5 skeletons.\n✓ Conversation: follow clear native speech, cope with surprises — Intermédiaire scenarios.\n✓ Writing: connected paragraphs on familiar topics — U8 rédaction.\n\nThe U6 test: all 12 scenarios, Intermédiaire, chained, no choice crutches. Pass that end-to-end and you ARE B1 — the DELF certificate only documents it.\n\n⚠️ Common error: counting lessons instead of abilities — 100 done lessons with shaky recall ≠ B1. 🧠 Trick: monthly self-test — one scenario, one story, one opinion, no help. Pass = progress. 📝 Exam link: DELF B1 mirrors these exact tasks."
            )
        ),
        quiz = listOf(
            QuizQ("Shadowing means…", listOf("Reading silently", "Repeating 1 beat behind audio", "Translating"), 1, "Mimic melody + speed whole. 5 min daily retrains the ear."),
            QuizQ("« J'mange pas » vs « Je ne mange pas »:", listOf("Wrong vs right", "Fast speech vs full form", "Past vs present"), 1, "Dropped ne = spoken French. Exams write it, streets drop it. Know both."),
            QuizQ("B1 speaker can…", listOf("Only greet", "Handle travel + opinions with reasons", "Write novels"), 1, "Independence: travel, stories, opinions, clear native speech."),
            QuizQ("Marathon review pulls…", listOf("Random phrases", "Your lowest-box phrases", "Only new words"), 1, "Lowest Leitner box = most forgotten = highest priority."),
            QuizQ("Intermédiaire register adds…", listOf("Nothing", "Passé composé + futur proche, longer sentences", "Only slang"), 1, "Natural length, mixed tenses, fewer hand-holding repeats."),
            QuizQ("Panic at fast audio? First move:", listOf("Give up", "Catch nouns + verbs, let grammar blur", "Translate all"), 1, "Content words carry meaning. Catch them, reconstruct the rest."),
            QuizQ("« Chained scenarios » in u6 means…", listOf("Random order", "All 12 back-to-back, Intermédiaire only", "Easier mode"), 1, "One long session, no training wheels. The capstone test."),
            QuizQ("Typing over multiple choice builds…", listOf("Slower fingers", "Recall (production) vs recognition", "Nothing"), 1, "Recognition is easy; production is fluency. u6 forces production.")
        )
    ),
    UnitGuide(
        "u7", "Grammaire", "Grammar A1→B2", "Sarufi",
        objectives = listOf(
            "Apply gender/number agreement automatically in writing",
            "Conjugate all groups + the 4 workhorses in key tenses",
            "Wield subjunctive triggers and si-clause pairs"
        ),
        notes = listOf(
            NoteSection(
                "Grammar as toolkit: the pattern-first method",
                "Rules stick when they explain sentences you already say. The method per U7 lesson:\n\n1. MEET 10+ real phrases using the pattern (articles in greetings, passé composé in stories).\n2. SPOT the pattern: what repeats? (« …est allé, …est venue — être + ending that changes! »)\n3. NAME the rule in one line: « être-verbs agree with the subject. »\n4. DRILL inside new phrases — never naked tables. Generate 5 of your own.\n5. HUNT errors: re-read old writing, circle violations of this rule only.\n\nWhy it works: pattern → rule → production mirrors how children acquire language, with adult speed. Tables are reference, not study material — consult, don't memorize.\n\n⚠️ Common error: collecting rules without producing sentences — knowledge that never fires. 🧠 Trick: every rule learned must produce 5 new sentences within 24h, or it's forgotten. 📝 Exam link: papers test rules INSIDE sentences — train the same way."
            ),
            NoteSection(
                "The 5 sentence machines that generate half of French",
                "Master these frames and you can say almost anything at A2–B1:\n\n1. IDENTIFY: c'est + noun — « C'est mon frère. C'est un examen difficile. » (Never *il est un…)\n2. DESCRIBE: il/elle est + adjective — « Elle est gentille. C'est facile. »\n3. EXIST: il y a + noun — « Il y a un problème. Il n'y a pas de bus. »\n4. NECESSITATE: il faut que + SUBJUNCTIVE — « Il faut que tu viennes. »\n5. DREAM: si + imparfait → conditionnel — « Si j'avais le temps, je voyagerais. »\n\nEach machine has exactly one trap: (1) c'est vs il est, (2) agreement (gentille), (3) pas de after negation, (4) subjunctive mood, (5) never si + conditionnel.\n\nWorked chain: « C'est mon ami (1). Il est sympa (2). Il y a une fête samedi (3). Il faut que tu viennes (4). Si tu viens, on dansera (5, real condition). » — five machines, one paragraph.\n\n⚠️ Common error: mixing machines (*il est un problème, *si tu viendrais). 🧠 Trick: label every sentence you write with its machine number for a week. 📝 Exam link: these frames ARE the composition backbone."
            ),
            NoteSection(
                "Agreement: the silent spelling examiners see",
                "Spoken French hides agreement; written French shows it. The full map:\n\n• Adjectives: +e feminine, +s plural — content/contente/contents/contentes.\n• être-verbs: agree with SUBJECT — Elle est allée, Ils sont partis.\n• avoir-verbs: agree ONLY with PRECEDING object — « les mangues que j'ai mangées » vs « J'ai mangé les mangues » (nothing).\n• Reflexives: usually subject — « Elle s'est levée »; exception: following object — « Elle s'est lavé les mains » (hands after → no agreement).\n• Past participles alone as adjectives: « une porte ouverte ».\n\nThe 5-minute re-read protocol (exam eve + paper end): sweep verbs→subjects, adjectives→nouns, participles→auxiliaries. Three passes, +10% marks.\n\n⚠️ Common error: agreeing avoir-participles with the subject (*ils ont allés is right for aller — être! — but *ils ont mangés is wrong). Ask auxiliary FIRST. 🧠 Trick: « preceding object? » — point left of the verb; nothing there → no agreement. 📝 Exam link: agreement items are the highest-density marks in Paper 2."
            ),
            NoteSection(
                "Subjunctive map: feelings about reality",
                "Indicative states facts; subjunctive colors them with desire, doubt, emotion, necessity:\n\n• DESIRE: vouloir que, souhaiter que, désirer que — « Je veux que tu réussisses. »\n• DOUBT: douter que, il est possible que — « Je doute qu'il vienne. » (but: je ne doute pas que → indicative!)\n• EMOTION: avoir peur que, être content que, regretter que — « J'ai peur qu'il pleuve. »\n• NECESSITY: il faut que, il est temps que — « Il faut que tu partes. »\n• CONCESSION/PURPOSE: bien que, pour que, avant que, sans que.\n\nTop-5 subjunctive forms (half the marks): fasse, soit, ait, aille, puisse. Irregular stems: prend- → prenne, vienn- → vienne, reçoiv- → reçoive.\n\n⚠️ Common error: après que + subjunctive — WRONG, it takes indicative (vs avant que + subj). The famous trap pair. 🧠 Trick: « BQ-PAINS » — Bien que, Pour que, Avant que, Il faut que, Non-doute (je doute que), Sans que → subj. 📝 Exam link: one correct subjunctive in a composition signals B2 instantly."
            )
        ),
        quiz = listOf(
            QuizQ("« C'est ___ examen difficile » (masc.):", listOf("une", "un", "de"), 1, "examen is masculine: un examen. c'est + un/une + noun."),
            QuizQ("« Elles sont ___ » (tired, fem pl):", listOf("fatigué", "fatiguées", "fatiguer"), 1, "Feminine +e, plural +s: fatiguées. Agreement with être."),
            QuizQ("« Il faut que tu ___ »:", listOf("viens", "viennes", "venir"), 1, "il faut que → subjunctive. viennes, fasses, sois, ailles."),
            QuizQ("« Si j'avais le temps, je ___ »:", listOf("voyage", "voyagerais", "voyagerai"), 1, "si + imparfait → conditionnel. Never si + conditionnel."),
            QuizQ("COD before auxiliary: « les lettres que j'ai ___ »:", listOf("écrit", "écrites", "écrire"), 1, "Preceding feminine-plural object → écrites. The B1 trap."),
            QuizQ("« Je lui parle » — lui means…", listOf("him only", "to him OR her", "them"), 1, "parler À → lui (both genders). Context decides."),
            QuizQ("« Ne me quitte pas » — order of negation:", listOf("ne + me + verb + pas", "me + ne + verb", "ne + verb + me + pas"), 0, "Pronouns stay glued pre-verb: Ne me regarde pas !"),
            QuizQ("« Ce qui compte » vs « ce que je veux »:", listOf("Same", "Subject vs object 'what'", "Formal vs slang"), 1, "ce qui (does) / ce que (receives) / ce dont (de-verbs).")
        )
    ),
    UnitGuide(
        "u8", "Lycée", "High school / KCSE", "Shule ya upili",
        objectives = listOf(
            "Attack all three papers with method (questions first, justify, never blank)",
            "Write formal letters + structured compositions with connectors",
            "Survive the oral: presentation, picture, conversation"
        ),
        notes = listOf(
            NoteSection(
                "Paper method: the 6 habits worth more than vocabulary",
                "1. QUESTIONS FIRST — listening and reading: read all questions, underline qui/quand/combien/où, THEN meet the text. You hunt with targets.\n2. STAY IN THE FENCE — « d'après le 2e paragraphe » means answers outside it score zero even if true.\n3. RELEVER ≠ TROUVER — relever/citer = copy word-for-word in quotes; trouver/expliquer = your own words. Wrong move, lost marks.\n4. VRAI/FAUX + PROOF — verdict + 5–8 quoted words. No quote = half marks, always.\n5. NEVER BLANK — a guess can earn method marks; blanc = 0 garanti.\n6. FINAL 5 MINUTES — agreement sweep: verbs→subjects, adjectives→nouns, participles→auxiliaries.\n\nTime plan (2h paper): comprehension 35 min, grammar 25, composition 50 (10 plan + 35 write + 5 check), buffer 10.\n\n⚠️ Common error: answering from general knowledge instead of the text — examiners mark the TEXT, not the world. 🧠 Trick: every answer must point at a line number. No line → no answer. 📝 Exam link: this IS the Paper 2 marking scheme."
            ),
            NoteSection(
                "Letters: two architectures, zero improvisation",
                "FORMELLE (job, principal, officials):\n• En-tête top-right: « Nairobi, le 3 mars 2026 »\n• Titre: « Monsieur le Directeur, » / « Madame la Proviseure, »\n• Opener: « Je me permets de vous écrire au sujet de… »\n• Body: situation → request → justification (one paragraph each)\n• Close: « Dans l'attente de votre réponse, veuillez agréer, Monsieur, l'expression de mes salutations distinguées. »\n• Signature: full name + class/form + phone.\n\nAMICALE (friends, host family):\n• « Mon cher Achieng, » + thanks for last letter + 2 news items + 2 questions (« Donne-moi de tes nouvelles ! ») + promise (« Réponds-moi vite ! ») + « Bises » / « Amitiés ».\n\n⚠️ Common error: « Cher Monsieur » to officials (too intimate) or missing en-tête (format marks lost). 🧠 Trick: formelle = 6 boxes; count yours before submitting. 📝 Exam link: letter format carries ~30% of the letter's marks."
            ),
            NoteSection(
                "Compositions: the B+ recipe, step by step",
                "INTRO (3 sentences): hook (question or fact) → reformulate subject → announce plan (« D'abord…, ensuite…, enfin… »).\nBODY (3 paragraphs, one idea each):\n• Connector ladder, one per paragraph IN ORDER: d'abord, ensuite/de plus, en revanche/cependant, enfin.\n• Each paragraph: claim → parce que → concrete example (Kisumu market, Kipchoge, Kibera school — proper nouns + numbers).\n• One subjunctive somewhere (« il faut que l'État agisse ») = examiner smile.\nCONCLUSION: bilan (« En somme… ») + avis (« À mon avis… ») + ouverture (« Il faudrait que… »). No new arguments.\n\nLength: 150 words ±10% → 15–18 lines. Count lines × ~10.\n\n⚠️ Common error: new idea in the conclusion, or 3 paragraphs all starting « De plus ». 🧠 Trick: write connectors FIRST as skeleton, then fill sentences. 📝 Exam link: plan + connectors + example + subjunctive = the visible marking grid."
            ),
            NoteSection(
                "The oral: smoothness beats perfection",
                "Three tasks, three playbooks:\n\n1. PRÉSENTATION (30s, memorized): « Je m'appelle Baraka, j'ai 17 ans, j'habite à Nakuru, je suis en quatrième année. » Name, age (avoir!), home, class. Smile on « Enchanté. »\n2. IMAGE (2 min, 5 beats): général → gauche/droite → arrière-plan → action (present!) → opinion (« À mon avis… » + justification).\n3. CONVERSATION: reformulate (« Si j'ai bien compris, vous demandez si… »), answer with parce que, ask one back (« Et vous ? »).\n\nSurvival kit (ALL allowed, ALL scored as strategy): « Pouvez-vous répéter, s'il vous plaît ? » / « Ça veut dire quoi… ? » / « Comment dit-on… en français ? » Fillers over silence: euh, alors, en fait, tu vois.\n\n⚠️ Common error: frozen silence after a hard question — zero for the item AND breaks rhythm. 🧠 Trick: nerves named aloud (« Je suis un peu nerveux, mais prêt ») disarm the room and buy 10 seconds. 📝 Exam link: strategy behaviors carry explicit marks."
            )
        ),
        quiz = listOf(
            QuizQ("First move in comprehension:", listOf("Read text fully", "Read questions first", "Write answers"), 1, "Questions first — hunt with targets, underline qui/quand/combien."),
            QuizQ("« Vrai ou faux ? Justifiez » needs…", listOf("Verdict only", "Verdict + quoted line", "Long essay"), 1, "5–8 quoted words as proof. No quote = half marks."),
            QuizQ("Formal letter opener:", listOf("« Salut ! »", "« Je me permets de vous écrire »", "« Yo »"), 1, "The KCSE formal key. Memorize exactly."),
            QuizQ("Essay skeleton:", listOf("Random paragraphs", "Intro + d'une part/d'autre part + conclusion", "One long paragraph"), 1, "Plan announced, balanced, concluded. Examiners scan for it."),
            QuizQ("Picture description order:", listOf("Random details", "General → anchors → action → opinion", "Opinion only"), 1, "Centre → gauche/droite → arrière-plan → à mon avis. Five beats."),
            QuizQ("Dictation: « ils parlent » sounds like…", listOf("« il parlait »", "« il parle »", "« ils parlent » (different)"), 1, "-ent is silent. Subject pronoun is your only clue — listen to the start."),
            QuizQ("Blank answer earns…", listOf("Zero, always", "Half marks", "Sympathy"), 0, "A guess can earn method marks. Blanc = 0 garanti."),
            QuizQ("Oral survival kit includes…", listOf("Silence", "« Pouvez-vous répéter ? »", "Leaving"), 1, "Repetition requests are ALLOWED. Strategy, not weakness.")
        )
    ),
    UnitGuide(
        "u9", "Prononciation", "Sounds & rhythm", "Matamshi",
        objectives = listOf(
            "Produce the 4 nasal vowels distinctly (vent vs vin)",
            "Roll the French R from the throat, link with liaisons",
            "Speak in rhythmic groups with French intonation"
        ),
        notes = listOf(
            NoteSection(
                "Nasal vowels: the 4-sound system with drills",
                "French has 4 nasals (air through nose AND mouth):\n• [ɑ̃] an/en: vent, enfant, temps, Jean\n• [ɔ̃] on: bon, maison, nom, mon\n• [ɛ̃] in/ain/ein: vin, pain, plein, jardin\n• [œ̃] un: brun, lundi (merging into [ɛ̃] in modern speech — brun ≈ brin)\n\nKILLER rule: n/m before p/b/m stays ORAL — « comme », « pomme », « bonne » are NOT nasal. Nasal only before other consonants or end: vent ✓, vendre ✓, comme ✗.\nFinal consonants after nasals: silent — vent (no t), dent (no t), long (no g), rond (no d). Say the nasal, STOP.\n\nDaily drill ladder:\n1. Minimal pairs: vent/vin, bon/bain, sans/sein, cent/sang.\n2. Hand on nose — feel the buzz on nasals, none on « comme ».\n3. Chain: « Un bon vin blanc » ([œ̃]-[ɔ̃]-[ɛ̃]-[ɑ̃] — all four in five words!).\n\n⚠️ Common error: nasalizing « comme/pomme/femme » (English habit). 🧠 Trick: see p/b/m after n/m → mouth only, nose off. 📝 Exam link: vent/vin and bon/bain dictations separate A-students yearly."
            ),
            NoteSection(
                "The French R: throat mechanics + cluster drills",
                "Uvular [ʁ]: back-of-throat friction, like a soft gargle. Swahili/English trilled or tapped R must go. Steps: (1) gargle water, feel the spot; (2) dry-gargle « ra »; (3) add vowels: ra, re, ri, ro, ru; (4) words.\n\nCluster ladder (consonant + R, one tight syllable — no extra vowel, not *tirois):\n• tr-/dr-: trois, très, droit\n• fr-/vr-: frère, froid, vrai\n• pr-/br-: prendre, propre, bras\n• cr-/gr-: craindre, grand, gris\n\nR before consonants softens to breath: porte [pɔʁt], marcher, art. Final -re: quatre [katʁ], notre, votre — half-swallowed.\n-er verbs vs -eur nouns: parler (verb, R soft) vs professeur (noun, R colours vowel).\n\n⚠️ Common error: rolling R Spanish-style — instantly foreign. 🧠 Trick: whisper « ach » (German-style) then voice it — that's [ʁ]. 📝 Exam link: time-telling (quatre heures) and directions (rue, droite) are R obstacle courses in orals."
            ),
            NoteSection(
                "Liaisons: the three laws",
                "LAW 1 — MANDATORY (never skip): determiner/adjective + noun (les‿amis, deux‿enfants, grand‿homme), pronoun + verb (nous‿avons, ils‿ont, on‿a), verb + following (c'est‿un, sont‿allés), short adverb + adjective (très‿important, trop‿absent).\nLAW 2 — FORBIDDEN (never link): after « et » (et‿ami = instant error), after chez/nom propre in formal style, after long adverbs (lentement‿arrivé = no), before h-aspiré (les // héros, le // hier).\nLAW 3 — OPTIONAL (link = formal/elegant): « pas‿encore », « temps‿en temps », plural nouns + adjectives in careful speech.\n\nSounds: -s/-x → [z] (les‿amis), -t/-d → [t] (grand‿homme), -n → [n] (on‿a), -r/-g rare (long‿été).\nDictation superpower: hearing [z] PROVES a plural — « les‿enfants » writes the -s for you.\n\n⚠️ Common error: linking after et, or skipping les‿amis. 🧠 Trick: et is a WALL — full stop, breath, continue. 📝 Exam link: Paper 1 dictations hinge on heard liaisons."
            ),
            NoteSection(
                "Rhythm, stress, intonation: the music system",
                "STRESS: last syllable of each rhythmic group, always — fran-ÇAIS, je-PARLE, à la GARE. No English-style word stress, no Swahili penultimate habit.\nTIMING: syllable-timed — every syllable equal length. Clap « le-pe-tit-chat-est-mort » (6 even beats) vs English « LIT-tle CAT is DEAD » (squeezed). March, don't bounce.\nGROUPS: commas and syntax mark breaths — « Qu'est-ce que tu fais | ce soir ? » One stress per group, glide inside.\nMELODY: rise [↗] = question/continuation (« Tu viens ? » « D'abord… ↗ »), fall [↘] = statement/end (« Je viens. »), punch-fall = exclamation (« C'est pas POSsible ! »).\nFILLERS keep the floor: euh, ben, alors, quoi, tu vois — fluent stalling beats perfect silence.\n\n⚠️ Common error: stressing every word (English transfer) — sounds aggressive and breaks comprehension. 🧠 Trick: hum the sentence first (melody only), then add words. 📝 Exam link: oral fluency marks ARE rhythm + intonation marks."
            )
        ),
        quiz = listOf(
            QuizQ("« vent » vs « vin » differ in…", listOf("Spelling only", "Nasal vowel: [ɑ̃] vs [ɛ̃]", "Length"), 1, "vent [vɑ̃], vin [vɛ̃]. The most-tested nasal contrast."),
            QuizQ("French R is…", listOf("Tongue trill", "Throat gargle [ʁ]", "Silent"), 1, "Uvular. Gargle water, add vowels: ra-re-ri."),
            QuizQ("Which liaison is MANDATORY?", listOf("les‿amis", "et‿ami", "chez‿eux (formal: no)"), 0, "Article + noun always links. et is a wall — never."),
            QuizQ("« tu » [ty] vs « tout » [tu]:", listOf("Same", "Tight lips vs round lips", "Loud vs soft"), 1, "u [y] tight, ou [u] round. Minimal pair #1."),
            QuizQ("French stress falls on…", listOf("First syllable", "Last syllable of group", "Loudest word"), 1, "fran-ÇAIS, je-PARLE. Final always."),
            QuizQ("Shadowing = …", listOf("Silent reading", "Repeat 1 beat behind audio", "Slow playback"), 1, "Copies melody + liaisons whole. 5 min daily."),
            QuizQ("« œufs » (plural) sounds…", listOf("[ø] closed, no f", "[œf] with f", "[o] open"), 0, "les œufs [ø] vs un œuf [œf]. Number changes sound."),
            QuizQ("Question intonation…", listOf("Falls", "Rises", "Stays flat"), 1, "Rise [↗] = question. Same words, melody decides.")
        )
    ),
    UnitGuide(
        "u10", "Afrique francophone", "French that pays", "Afrika ya Kifaransa",
        objectives = listOf(
            "Discuss DRC, West Africa and Rwanda in French",
            "Write work/study French: CV lines, bourses, interviews",
            "Travel francophone Africa: tickets, hotels, borders"
        ),
        notes = listOf(
            NoteSection(
                "The DRC: your giant francophone neighbour",
                "Scale: ~100M people, Africa's 2nd-largest country, bordering Kenya via Uganda/Rwanda corridors. Languages: French (official, offices/schools) + 4 national (lingala, swahili, kikongo, tshiluba). Goma/Bukavu Swahili ≈ YOUR Swahili — instant bridge.\n\nWhere French pays:\n• TRADE: Busia–Malaba–Goma corridors; la facture, le devis, la livraison, payer en dollars.\n• MINING/NGOs: le coltan, le cobalt, la concession, l'appel d'offres, le rapport trimestriel.\n• MUSIC: rumba (Franco, Tabu Ley, Fally) mixes French + lingala — learn French FROM songs you already love.\n• BORDER: le visa, le tampon, les frais, la douane — crossing in French = respect + speed.\n\nCareer path: Swahili (street) + French (office) + trade English = employable Goma→Lubumbashi→Kinshasa.\n\n⚠️ Common error: assuming English works everywhere there — it doesn't; French is the professional gate. 🧠 Trick: learn 20 trade nouns (facture, stock, livraison…) and you're already useful. 📝 Exam link: DRC topics fit economy, culture AND geography essays."
            ),
            NoteSection(
                "West Africa: Dakar, Abidjan and the CFA zone",
                "SENEGAL: Dakar, Wolof + French, la Teranga (hospitality — same values as Kenyan karibu), UCAD university (scholarships!), mbalax music, thiéboudienne Fridays.\nCÔTE D'IVOIRE: Abidjan (economic capital) + Yamoussoukro, world #1 cocoa, coupé-décalé, and NOUCHI — Ivorian urban slang, their Sheng: young, mixed, fully legit. Lesson: French lives in streets, not books.\n\nMONEY: le franc CFA (XOF west / XAF central), 14 countries, fixed to the euro. Vocabulary: le taux, convertir, le billet, le transfert.\nBLOCS: la CEDEAO, l'AES (Mali–Burkina–Niger), l'UA — read Jeune Afrique headlines for B2 current-affairs French.\n\n⚠️ Common error: treating Africa as one topic — Dakar ≠ Abidjan ≠ Kinshasa; name specifics. 🧠 Trick: one dish + one artist + one fact per country = instant depth (thiéboudienne/Youssou N'Dour/Teranga). 📝 Exam link: regional blocs and CFA appear in news-based comprehension."
            ),
            NoteSection(
                "Rwanda: the neighbour that switched languages",
                "History in one paragraph: French-speaking until 2008, then English official; French still spoken by the older generation, the Church, DRC traders, and OIF membership. Kigali: propre, sûr, vallonné — le pays des mille collines.\n\nPractical French:\n• BORDER (Gatuna/Katuna): le passeport, le laissez-passer EAC, le contrôle — EAC ID works, French politeness speeds stamps.\n• COFFEE: la coopérative, la récolte, l'exportation — Rwanda's hills = quality vocabulary.\n• TOURISM: le permis (gorillas), le guide, le briefing — trilingual guiding pays.\n• SOLEMN REGISTER (genocide remembrance — handle with gravity): se recueillir, rendre hommage, ne jamais oublier, Kwibuka (se souvenir).\n• One Kinyarwanda word opens doors: « Murakaza neza ! » (= Bienvenue) — like Sheng at home, local words prove respect.\n\n⚠️ Common error: flippant tone on solemn topics — register must turn grave. 🧠 Trick: EAC = English + French working pair (DRC + Burundi at the table). 📝 Exam link: EAC/regional topics suit civics-flavoured essays."
            ),
            NoteSection(
                "Professional French: templates from CV to contract",
                "EMAIL: Objet: Candidature — [poste]. « Ci-joint mon CV et ma lettre. Cordialement, [Nom] [téléphone]. »\nCV LINE (top, bold): « Trilingue anglais–swahili–français (B2). » — your unfair advantage, stated first.\nINTERVIEW (90 seconds): formation (passé composé) → expérience (présent) → projet (futur). « Parlez-moi de vous » answered in three tenses.\nMEETING: l'ordre du jour, prendre la parole, le compte-rendu (PV), lever la séance.\nCONTRACT: le salaire brut/net, la période d'essai, les congés — read every clause, in French.\nSTUDY: la bourse (apply before March!), le campus, la résidence CROUS, Campus France interview IN FRENCH — oral prep pays directly.\nDELF B2: 50/100, no section under 5 — this app's track IS the prep.\n\n⚠️ Common error: « Bises » to recruiters (friends-only!) — sign-off ladder: Bises → Cordialement → distinguées. 🧠 Trick: templates, not talent — memorize 5 documents once, reuse forever. 📝 Exam link: formal letters and CV tasks recycle these exact formulas."
            )
        ),
        quiz = listOf(
            QuizQ("Goma street language + office language:", listOf("Swahili + French", "English only", "Lingala only"), 0, "Swahili outside, French inside. Both = hireable."),
            QuizQ("Nouchi is…", listOf("A dish", "Ivorian urban slang (≈Sheng)", "A currency"), 1, "Abidjan's street French. French lives in streets, not books."),
            QuizQ("Email sign-off for jobs:", listOf("« Bises »", "« Cordialement »", "« Yo »"), 1, "Bises (friends) → Cordialement (pro) → distinguées (formal)."),
            QuizQ("DELF B2 pass rule:", listOf("100/100", "50/100, no section under 5", "Just show up"), 1, "Balanced pass — no section may collapse."),
            QuizQ("« Ci-joint mon CV » means…", listOf("See my CV never", "Attached is my CV", "I have no CV"), 1, "Objet + Ci-joint + Cordialement. The application trio."),
            QuizQ("CFA franc zone covers…", listOf("Kenya", "14 African countries", "France only"), 1, "XOF/XAF, fixed to the euro. Money French: taux, convertir."),
            QuizQ("Interview « Parlez-moi de vous »: answer in…", listOf("90s past→present→future", "Salary first", "Silence"), 0, "Formation → expérience → projet. Three tenses, one hire."),
            QuizQ("Rwanda border French needs…", listOf("Nothing", "Passeport/EAC ID + French politeness", "A visa always"), 1, "Gatuna: documents + « Bonjour monsieur ». Respect speeds stamps.")
        )
    ),
    UnitGuide(
        "u11", "Maîtrise B2", "B2 mastery grammar", "Umahiri wa B2",
        objectives = listOf(
            "Report speech with correct tense shifts",
            "Use passive, gérondif and advanced compound tenses",
            "Control registers + advanced subjunctive"
        ),
        notes = listOf(
            NoteSection(
                "Reported speech: the full shift machine",
                "Present reporting verb (dit, demande) → NO shift: « Il dit qu'il viendra. » Past reporting verb (a dit, a demandé) → shift EVERYTHING one step back:\n\n• présent → imparfait: « J'ai faim » → « Il a dit qu'il avait faim. »\n• passé composé → plus-que-parfait: « J'ai mangé » → « …qu'il avait mangé. »\n• futur → conditionnel: « Je viendrai » → « …qu'elle viendrait. »\n• impératif → de + infinitif: « Tais-toi ! » → « Il dit de se taire. »\n• questions keep their word, flatten order: « Où habites-tu ? » → « Il a demandé où j'habitais. »\n• yes/no → si: « Tu viens ? » → « Dis-moi si tu viens. » (present) / « …si je viendrais » (past)\n\nTime/place words shift too: hier → la veille, demain → le lendemain, ici → là.\n\n⚠️ Common error: shifting under present verbs (*il dit qu'il avait faim) or forgetting si for yes/no. 🧠 Trick: check the REPORTING verb's tense first — present = freeze, past = shift. 📝 Exam link: « mettez au discours indirect » is a guaranteed transform exercise."
            ),
            NoteSection(
                "Compactness: gérondif, participles, infinitives",
                "B2 compresses. Three tools:\n\n1. GÉRONDIF (en + -ant, same subject, simultaneous): « En mangeant, il parle. » → « Tout en travaillant, elle étudie » (emphasis). Proverb frame: « C'est en forgeant qu'on devient forgeron. »\n2. PARTICIPE PRÉSENT (replaces qui-clauses): « un élève ayant fini… » (= qui a fini). sachant, ayant, étant, pouvant. Detached snapshots: « La réunion finie, on est partis. »\n3. INFINITIVE PASTS: « Après avoir mangé… » (= après que j'ai mangé), « Avant de partir… », « Peur d'avoir raté… »\n\nRewrite ladder (same meaning, shrinking): « Quand il a fini de manger, il est parti » (B1, 10 words) → « Après avoir mangé, il est parti » (B2, 6 words).\n\n⚠️ Common error: gérondif with different subjects (*en mangeant, il m'a appelé — who ate?). Same subject only. 🧠 Trick: hunt « quand/parce que/qui » in your drafts — each is a compression opportunity. 📝 Exam link: « reformulez en une phrase » tests exactly this."
            ),
            NoteSection(
                "Regret and hindsight: the full system",
                "Three structures, three shades:\n\n1. SI-REGRET (unreal past): si + plus-que-parfait → conditionnel passé. « Si j'avais su, je serais venu. » Never conditionnel after si — tattoo it.\n2. MODAL HINDSIGHT: aurait dû (should have), aurait pu (could have), aurait fallu (needed to). « J'aurais dû réviser. » / « Il aurait pu pleuvoir. »\n3. WISH-PAST: « J'aurais voulu être là. » (would have liked) — polite regret.\n\nProbability past: futur antérieur — « Il aura oublié, c'est sûr » (= he must have forgotten). Elegant supposition.\nDerby post-mortem, fully loaded: « Si l'arbitre avait sifflé, on aurait gagné. J'aurais dû y aller. Il aura plu sur le stade — quelle soirée ! »\n\n⚠️ Common error: *« Si j'aurais su » — the single most-circled B2 error in Kenya. 🧠 Trick: SI loves IMPARFAIT-family (imparfait, plus-que-parfait); CONDITIONNEL lives in the RESULT clause only. 📝 Exam link: si-clause transforms appear in every B2 paper."
            ),
            NoteSection(
                "Register wardrobe: dress every sentence",
                "Three wardrobes, mix at your peril:\n\nSOUTENU (institutions, elders, exams): vous, inversion (« Pourrais-je… ? »), veuillez + infinitif, prière de, ne…pas kept, « Je vous prie d'excuser… », connectors (en somme, dès lors, force est de constater).\nSTANDARD (neutral default): tu/vous correctly, est-ce que, Cordialement, « À mon avis… parce que… ».\nFAMILIER (friends ONLY): tu, salut/coucou, verlan (meuf, ouf), dropped ne (t'inquiète), bosser/bouffer/bagnole, kif-kif, se marrer.\n\nSwitch drills (same message, three clothes):\n• Wait: « Veuillez patienter » / « Un moment, s'il vous plaît » / « Attends deux secondes ! »\n• Thanks: « Je vous suis reconnaissant » / « Merci beaucoup » / « Merci mec ! »\n• Leave: « Je prends congé » / « Je dois y aller » / « Je me casse ! » (very familiar!)\n\n⚠️ Common error: slang in compositions, soutenu with friends (sounds mocking). 🧠 Trick: the principal test — say it to the principal? No → don't write it. 📝 Exam link: register appropriateness is explicitly marked."
            )
        ),
        quiz = listOf(
            QuizQ("« Je viendrai » → reported past:", listOf("qu'elle viendra", "qu'elle viendrait", "qu'elle vienne"), 1, "Futur → conditionnel under past reporting."),
            QuizQ("« En mangeant, il parle » — same subject doing…", listOf("One thing", "Two things at once", "Nothing"), 1, "Gérondif = simultaneity, same subject. Compact elegance."),
            QuizQ("Ultimate regret:", listOf("« Si j'aurais su »", "« Si j'avais su, je serais venu »", "« Je regrette »"), 1, "si + PQP, never conditionnel after si. Tattoo it."),
            QuizQ("Passive agrees with…", listOf("The agent", "The subject", "Nothing"), 1, "La loi a été votée. être + participle matching the subject."),
            QuizQ("« Il vaut mieux que tu dormes » — mood?", listOf("Indicative", "Subjunctive", "Infinitive"), 1, "valoir mieux que → subj. Advice with muscle."),
            QuizQ("Formal « wait »:", listOf("« Attends ! »", "« Veuillez patienter »", "« Bof »"), 1, "veuillez + infinitive: the institution's polite command."),
            QuizQ("« Je ne pense pas qu'il vienne » — why subjunctive?", listOf("Always after penser", "Negative opinion flips mood", "It's formal"), 1, "Affirmative penser = indic; negative/question = subj."),
            QuizQ("« Après avoir mangé » compresses…", listOf("Nothing", "« Après que j'ai mangé »", "The future"), 1, "Infinitive pasts: après avoir, avant de, peur d'avoir. Half the words.")
        )
    ),
    UnitGuide(
        "u12", "Idiomes", "Idioms & proverbs", "Misemo",
        objectives = listOf(
            "Deploy 30+ idioms (body, animals, food) naturally",
            "Crown essays and speeches with proverbs",
            "Read tone: familiar vs standard imagery"
        ),
        notes = listOf(
            NoteSection(
                "Idioms are pre-built fluency: the chunk method",
                "Natives speak in CHUNKS, not words. One idiom per conversation doubles perceived fluency with zero new grammar. Deployment rules: one per dialogue/paragraph (more = showing off), place at emotional peaks (joy, anger, relief), never translate literally.\n\nStarter arsenal (anywhere-register):\n• « C'est du gâteau. » (easy win) / « Les doigts dans le nez. » (playful ease)\n• « Couper la poire en deux. » (compromise — markets AND meetings)\n• « La cerise sur le gâteau. » (bonus on good news)\n• « Prendre son courage à deux mains. » (facing hard things — exam mornings)\n• « Se serrer les coudes. » (solidarity — chama spirit)\n• « Mettre son grain de sel. » (opinions, announced politely)\n\nPractice: retell yesterday using 3 idioms. « Le réveil a sonné (dur), j'ai pris mon courage à deux mains, le contrôle était du gâteau, et la cerise: le prof m'a félicité. »\n\n⚠️ Common error: word-for-word translation of Sheng/English idioms (*« tirer ma jambe » for pull my leg — say « me faire marcher »). 🧠 Trick: learn idioms as unbreakable blocks — never swap words inside. 📝 Exam link: one apt idiom per composition = flair marks."
            ),
            NoteSection(
                "The animal zoo: character in beasts",
                "French describes people through animals. The essential zoo:\n\n• têtu comme une mule (stubborn), doux comme un agneau (gentle), fier comme un paon (proud)\n• rusé comme un renard (cunning), fort comme un bœuf (strong), malin comme un singe (clever)\n• « Poser un lapin » (stand up), « avoir un chat dans la gorge » (frog in throat), « donner sa langue au chat » (give up guessing)\n• « Un froid de canard » (bitter cold), « avoir le cafard » (the blues), « être le dindon de la farce » (fall guy)\n• « Petit à petit, l'oiseau fait son nid » (patience — essay gold)\n\nUsage frames: « Il est têtu comme… » (he is), « Ça me donne… » (it gives me), standalone proverbs to close.\nExam-eve message to a friend: « Prends ton courage à deux mains, donne le meilleur de toi — petit à petit, l'oiseau fait son nid ! » — three idioms, one text.\n\n⚠️ Common error: inventing animal similes (*fort comme un lion passes actually — but *rapide comme un escargot ironically means slow!). Check meaning before deploying. 🧠 Trick: learn each beast with its HUMAN trait, not its biology. 📝 Exam link: descriptions (people, characters) glow with one animal simile."
            ),
            NoteSection(
                "Proverbs: closers that examiners remember",
                "Endings stick. Deploy by essay topic:\n\n• HARDSHIP/loss: « Après la pluie, le beau temps. »\n• EFFORT/progress: « Petit à petit, l'oiseau fait son nid. »\n• JUDGMENT/appearances: « L'habit ne fait pas le moine. »\n• PATIENCE: « Tout vient à point à qui sait attendre. » / « Qui vivra verra. »\n• GRATITUDE (bourses, gifts): « À cheval donné, on ne regarde pas les dents. »\n• ACTION: « Mieux vaut prévenir que guérir. » (health/environment)\n• COMMUNITY: « L'union fait la force. » (chama, teamwork topics)\n• WISDOM-twins (bilingual flex): French proverb + Kenyan twin — « Petit à petit… — kama Haraka haraka haina baraka. »\n\nFull worked conclusion: « En somme, l'année fut dure (bilan). À mon avis, elle m'a rendu plus fort (avis). Après la pluie, le beau temps — comme on dit chez moi, baada ya dhiki faraja (ouverture + twin). »\n\n⚠️ Common error: dropping proverbs mid-essay randomly — closers ONLY (or openers, never middle). 🧠 Trick: memorize 6 closers cold; assign each to a topic family. 📝 Exam link: conclusions decide final impressions — wisdom closes higher."
            ),
            NoteSection(
                "Food idioms + register: the tasty map",
                "French argues, loves and despairs through food:\n\n• ANGER: « La moutarde me monte au nez. » / « Être soupe au lait. » (quick temper) / « Bouillir de colère. »\n• LIES/TALK: « Raconter des salades. » / « Noyer le poisson. » (dodge the issue!)\n• GREED/LIMITS: « Vouloir le beurre et l'argent du beurre. » (debate weapon)\n• EASE: « C'est du gâteau. » / « C'est du tout cuit. »\n• DISASTER (playful): « C'est la fin des haricots ! »\n• CHARACTER: « Une bonne pâte. » (kind soul) / « Un cordon-bleu. » (great cook)\n\nREGISTER sorting (critical):\n• Anywhere: cerise sur le gâteau, beurre et argent du beurre, fin des haricots, bonne pâte.\n• Friends: casser les pieds, se marrer, kif-kif, bof, bouffer.\n• Recognize-only (vulgar): merde, putain, con — understand films, never produce in class.\n\n⚠️ Common error: « bouffer » at a gala dinner or to elders — register violence. 🧠 Trick: food idioms are safe; BODY/annoyance idioms check the room first. 📝 Exam link: dialogues with friends reward one familiar idiom; formal tasks punish them."
            )
        ),
        quiz = listOf(
            QuizQ("« Poser un lapin » =", listOf("Cook rabbit", "Stand someone up", "Win"), 1, "The no-show verb. Victims: « On m'a posé un lapin. »"),
            QuizQ("Easy win — you say…", listOf("« C'est du gâteau »", "« C'est dur »", "Nothing"), 0, "Victory vocabulary: du gâteau, tout cuit, doigts dans le nez."),
            QuizQ("Compromise at the market:", listOf("« Non. »", "« Coupons la poire en deux ! »", "Leave"), 1, "Pear-halving idiom + smile = better price."),
            QuizQ("Essay on appearances — crown it:", listOf("« Bof »", "« L'habit ne fait pas le moine »", "« Salut »"), 1, "Anti-judgment proverb. Instant depth."),
            QuizQ("« Avoir un chat dans la gorge » — use when…", listOf("Hungry", "Throat blocked before speaking", "Tired"), 1, "Oral rescue: buys water + sympathy."),
            QuizQ("« Mieux vaut prévenir que guérir » fits…", listOf("Health essays", "Recipes", "Greetings"), 0, "Prevention beats cure. Health/environment topics."),
            QuizQ("« Les grands esprits se rencontrent » — when?", listOf("Someone shares your idea", "Goodbye", "Apology"), 0, "Agreement delight. Instant friendship French."),
            QuizQ("Hardship essay ending:", listOf("« C'est fini »", "« Après la pluie, le beau temps »", "« Bof »"), 1, "Hope proverb. Examiners remember endings.")
        )
    )
)