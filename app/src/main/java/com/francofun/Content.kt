package com.francofun

import java.text.Normalizer
import org.json.JSONArray
import org.json.JSONObject

enum class HelpLang(val label: String, val promptName: String) {
    ENGLISH("English", "English"),
    SWAHILI("Kiswahili", "Kiswahili (Swahili)"),
    SHENG("Sheng", "Sheng (Nairobi street slang: Swahili mixed with English and local slang, casual, fun and natural)")
}

fun HelpLang.t(en: String, sw: String, sheng: String = sw): String = when (this) {
    HelpLang.ENGLISH -> en
    HelpLang.SWAHILI -> sw
    HelpLang.SHENG -> sheng
}

data class Phrase(
    val fr: String,
    val en: String,
    val sw: String,
    val sheng: String? = null,
    val tip: String? = null,
    // §4.2: grammar note shown as "why is it like this?", CEFR tag for SRS pacing, core = highest-frequency
    val grammar: String? = null,
    val level: String = "A1",
    val core: Boolean = false
) {
    fun meaning(lang: HelpLang): String = when (lang) {
        HelpLang.ENGLISH -> en
        HelpLang.SWAHILI -> sw
        HelpLang.SHENG -> sheng ?: sw
    }
    fun key(): String = fr.trim().lowercase()
}

data class Lesson(
    val id: String,
    val emoji: String,
    val fr: String,
    val en: String,
    val sw: String,
    val phrases: List<Phrase>,
    val unitId: String = "u1",
    // §4.4: optional 2–4 sentence cultural aside, weighted to Francophone Africa
    val culture: String? = null
)

data class StudyUnit(val id: String, val emoji: String, val fr: String, val en: String, val sw: String, val lessonIds: List<String>)

data class Verb(
    val infinitive: String,
    val en: String,
    val sw: String,
    val present: Map<String, String>, // je, tu, il, nous, vous, ils
    val passe: Map<String, String> = emptyMap()
)

// Edit / extend freely. Sheng is optional per phrase (falls back to Swahili).
val LESSONS: List<Lesson> = listOf(
    Lesson("greet", "👋", "Salutations", "Greetings", "Salamu", listOf(
        Phrase("Bonjour", "Hello / Good day", "Habari / Hujambo", "Mambo", "Use until the evening, then switch to Bonsoir.", null, "A1", true),
        Phrase("Bonsoir", "Good evening", "Habari za jioni", null, "Evening greeting — and what you say when arriving after ~6pm."),
        Phrase("Salut", "Hi (informal)", "Sasa / Vipi", "Niaje", "Casual, with friends only.", null, "A1", true),
        Phrase("Coucou !", "Hey there! (cheerful)", "Mambo!", "Niaje!", "Extra friendly — friends and family, not officials."),
        Phrase("Bonjour madame", "Hello, madam", "Habari, mama", null, "Madame/monsieur matters in France — always add it with strangers."),
        Phrase("Bonjour monsieur", "Hello, sir", "Habari, mzee", null, "Same rule: greet + title, or you sound rude."),
        Phrase("Bienvenue !", "Welcome!", "Karibu!", null, null, null, "A1", true),
        Phrase("Comment ça va ?", "How are you?", "Habari yako?", "Uko aje?", null, null, "A1", true),
        Phrase("Comment allez-vous ?", "How are you? (formal)", "Habari yako? (heshima)", null, "Vous = respect or groups; tu = friends."),
        Phrase("Ça va bien, merci", "I'm fine, thanks", "Nzuri, asante", "Niko poa"),
        Phrase("Je vais bien", "I'm doing well", "Ni mzima", "Niko fiti"),
        Phrase("Et toi ?", "And you? (friend)", "Na wewe?", "Na wewe?", null, "Use 'et vous ?' with strangers.", "A1", true),
        Phrase("Et vous ?", "And you? (formal)", "Nawe? (heshima)"),
        Phrase("Quoi de neuf ?", "What's new?", "Kuna nini kipya?", "Kuna nini mpya?"),
        Phrase("Pas grand-chose", "Not much", "Hakuna kipya", "Hakuna kitu"),
        Phrase("Ravi de te voir", "Happy to see you", "Nimefurahi kukuona"),
        Phrase("Ça fait longtemps", "Long time no see", "Muda mrefu", "Muda!"),
        Phrase("Au revoir", "Goodbye", "Kwaheri", "Tuonane", null, null, "A1", true),
        Phrase("À bientôt", "See you soon", "Tutaonana hivi karibuni", "Tuonane soon"),
        Phrase("À plus tard", "See you later", "Tutaonana baadaye", "Tuonane later", "Often shortened to just 'À plus !' between friends."),
        Phrase("À demain", "See you tomorrow", "Tutaonana kesho", "Tuonane kesho"),
        Phrase("Bonne journée !", "Have a nice day!", "Uwe na siku njema!"),
        Phrase("Bonne soirée !", "Have a nice evening!", "Uwe na jioni njema!"),
        Phrase("Bonne nuit", "Good night", "Usiku mwema", null, "Only when someone is going to sleep — not as an evening hello."),
        Phrase("Désolé, je dois partir", "Sorry, I have to go", "Pole, lazima niende")
    ), "u1", "In France you greet shopkeepers the second you walk in — a loud 'Bonjour !' first, questions after. Silence reads as rudeness, the opposite of Kenya where a smile alone often does the job."),
    Lesson("polite", "🙏", "Politesse", "Politeness", "Adabu", listOf(
        Phrase("S'il vous plaît", "Please", "Tafadhali", null, "Literally 'if it pleases you' — the vous-form, safe everywhere.", null, "A1", true),
        Phrase("S'il te plaît", "Please (friend)", "Tafadhali", null, "Tu-form for friends and kids."),
        Phrase("Merci", "Thanks", "Asante", "Asante", null, null, "A1", true),
        Phrase("Merci beaucoup", "Thank you very much", "Asante sana", "Asante sana, manze"),
        Phrase("Merci bien", "Thanks a lot", "Asante sana"),
        Phrase("Mille mercis", "A thousand thanks", "Asante elfu", null, "Warm and enthusiastic, for real favours."),
        Phrase("Je vous remercie", "I thank you (formal)", "Nawashukuru"),
        Phrase("De rien", "You're welcome", "Karibu / Usijali", "Poa tu"),
        Phrase("Je vous en prie", "You're welcome (formal)", "Karibu sana", null, "The polite reply when someone thanks you formally."),
        Phrase("Avec plaisir", "With pleasure / gladly", "Kwa furaha", null, null, "A2"),
        Phrase("Il n'y a pas de quoi", "Don't mention it", "Hamna shida", null, null, "A2"),
        Phrase("Excusez-moi", "Excuse me", "Samahani", null, null, null, "A1", true),
        Phrase("Pardon", "Sorry", "Pole", "Pole"),
        Phrase("Désolé", "Sorry (I am sorry)", "Samahani", "Pole", "Désolé(e): add e if feminine.", null, "A1"),
        Phrase("Je m'excuse", "I apologize", "Naomba msamaha"),
        Phrase("Toutes mes excuses", "My sincerest apologies", "Samahani sana", null, "For serious mistakes — stronger than pardon."),
        Phrase("Ce n'est pas grave", "It's no big deal", "Hamna shida", "Si issue", null, null, "A2"),
        Phrase("Ne vous inquiétez pas", "Don't worry", "Usijali"),
        Phrase("Je ne comprends pas", "I don't understand", "Sielewi", "Sijaget", null, null, "A1", true),
        Phrase("Répétez, s'il vous plaît", "Repeat, please", "Rudia tafadhali", "Rudia tena"),
        Phrase("Parlez lentement, s'il vous plaît", "Speak slowly, please", "Tafadhali ongea polepole", "Ongea pole pole"),
        Phrase("Permettez-moi", "Allow me / excuse me (passing)", "Naomba kupita"),
        Phrase("Après vous", "After you", "Baada yako", null, "Say it with a small gesture when holding a door."),
        Phrase("C'est gentil", "That's kind of you", "Ukarimu wako"),
        Phrase("Pas de problème", "No problem", "Hamna shida", "Hamna noma")
    ), "u1"),
    Lesson("intro", "🙋", "Se présenter", "Introducing yourself", "Kujitambulisha", listOf(
        Phrase("Je m'appelle…", "My name is…", "Jina langu ni…", "Naitwa…", null, "Reflexive verb: s'appeler.", "A1", true),
        Phrase("Comment vous appelez-vous ?", "What is your name?", "Jina lako nani?", "Unaitwa aje?"),
        Phrase("Comment tu t'appelles ?", "What's your name? (friend)", "Jina lako nani?", "Unaitwa nani?"),
        Phrase("Enchanté", "Nice to meet you", "Nimefurahi kukutana nawe", "Nimefurahi kukuona", "Add an extra 'e' in writing if you're a woman: Enchantée."),
        Phrase("Je suis kényan(e)", "I am Kenyan", "Mimi ni Mkenya", null, "Add (e) if feminine: kényane."),
        Phrase("Je viens du Kenya", "I come from Kenya", "Ninatoka Kenya", null, null, "A1"),
        Phrase("Je suis né(e) à Nairobi", "I was born in Nairobi", "Nilizaliwa Nairobi", null, "Né (m) / née (f).", "A2"),
        Phrase("D'où viens-tu ?", "Where are you from?", "Unatoka wapi?", "Unatoka wapi?", null, null, "A1", true),
        Phrase("J'habite à Nairobi", "I live in Nairobi", "Ninaishi Nairobi", "Nakaa Nairobi"),
        Phrase("J'habite à Kawangware", "I live in Kawangware", "Ninaishi Kawangware", "Nakaa Kangwe", "Swap in your own estate — instant conversation starter."),
        Phrase("Je suis étudiant(e)", "I am a student", "Mimi ni mwanafunzi", null),
        Phrase("J'étudie l'ingénierie", "I study engineering", "Ninasomea uhandisi", "Nasoma engineering"),
        Phrase("Je travaille comme ingénieur", "I work as an engineer", "Ninafanya kazi kama mhandisi", null, null, "A2"),
        Phrase("Je cherche du travail", "I'm looking for work", "Ninatafuta kazi"),
        Phrase("J'ai vingt ans", "I'm twenty", "Nina miaka ishirini", null, "Age uses avoir: 'I have 20 years'.", "A1"),
        Phrase("Quel âge as-tu ?", "How old are you?", "Una miaka mingapi?"),
        Phrase("Je suis célibataire", "I'm single", "Sina mchumba", null, null, "A2"),
        Phrase("Voici mon ami", "This is my friend", "Huyu ni rafiki yangu", null, null, "A2"),
        Phrase("Je parle anglais et swahili", "I speak English and Swahili", "Ninaongea Kiingereza na Kiswahili", null, null, "A1"),
        Phrase("Je parle un peu français", "I speak a little French", "Ninazungumza Kifaransa kidogo", null),
        Phrase("J'apprends le français", "I'm learning French", "Ninajifunza Kifaransa", null, null, null, "A1", true),
        Phrase("Depuis quand ?", "Since when?", "Tangu lini?"),
        Phrase("Mon numéro est…", "My number is…", "Nambari yangu ni…", null, null, "A1"),
        Phrase("Je suis nouveau ici", "I'm new here", "Mimi ni mgeni hapa"),
        Phrase("Bienvenue au Kenya !", "Welcome to Kenya!", "Karibu Kenya!")
    ), "u1", "Kenyans abroad get instant warmth from saying where exactly they're from — 'Nakuru' beats 'Kenya' the same way 'Lyon' beats 'France'. Name your estate, your campus, your county."),
    Lesson("food", "🍽️", "Manger et boire", "Food and drink", "Chakula na vinywaji", listOf(
        Phrase("J'ai faim", "I'm hungry", "Nina njaa", null, "French says 'I have hunger', not 'I am hungry'.", null, "A1", true),
        Phrase("J'ai très faim", "I'm very hungry", "Nina njaa sana", "Nina njaa design"),
        Phrase("J'ai soif", "I'm thirsty", "Nina kiu", null, null, null, "A1"),
        Phrase("De l'eau, s'il vous plaît", "Water, please", "Maji tafadhali", "Nipe maji", null, null, "A1", true),
        Phrase("Un café, s'il vous plaît", "A coffee, please", "Kahawa tafadhali", null),
        Phrase("Un thé au lait", "A milky tea", "Chai ya maziwa", "Chai"),
        Phrase("Je voudrais du pain", "I would like some bread", "Ningependa mkate", "Nataka mkate"),
        Phrase("Je voudrais du riz", "I'd like some rice", "Ningependa wali", "Nataka wali"),
        Phrase("Le poulet", "Chicken", "Kuku", null, null, "A1"),
        Phrase("Le poisson", "Fish", "Samaki", null, null, "A1"),
        Phrase("La viande", "Meat", "Nyama"),
        Phrase("Les légumes", "Vegetables", "Mboga"),
        Phrase("Les fruits", "Fruit", "Matunda"),
        Phrase("C'est délicieux !", "It's delicious!", "Ni tamu sana!", null, null, null, "A1", true),
        Phrase("C'est trop salé", "It's too salty", "Ina chumvi nyingi"),
        Phrase("C'est trop sucré", "It's too sweet", "Ina sukari nyingi"),
        Phrase("C'est épicé", "It's spicy", "Ina pilipili", "Ina pilipili"),
        Phrase("L'addition, s'il vous plaît", "The bill, please", "Bili tafadhali", "Nipe bill", null, null, "A1", true),
        Phrase("On partage l'addition", "Let's split the bill", "Tugawane bili", "Tusplit bill"),
        Phrase("C'est moi qui invite", "It's my treat", "Mimi nitalipa", "Na-sort bill"),
        Phrase("Je suis végétarien(ne)", "I am vegetarian", "Mimi ni mlaji mboga", null),
        Phrase("Sans viande, s'il vous plaît", "No meat, please", "Bila nyama tafadhali"),
        Phrase("Bon appétit !", "Enjoy your meal!", "Chakula chema!", null, "Say it when food arrives — it's the law.", null, "A1", true),
        Phrase("Santé !", "Cheers!", "Maisha marefu!", "Cheers!"),
        Phrase("Encore un peu ?", "A little more?", "Nikuongezee kidogo?", null, "What Kenyan mums and French grandmas have in common.")
    ), "u2", "Food is diplomacy: in France lunch is sacred (never rush it), in Senegal refusing a second helping of thiéboudienne needs real skill, and everywhere, complimenting the cook — 'c'est délicieux !' — opens every door."),
    Lesson("city", "🚌", "En ville", "Getting around", "Mjini", listOf(
        Phrase("Où est la gare ?", "Where is the station?", "Stesheni iko wapi?", "Stage iko wapi?", null, null, "A1", true),
        Phrase("Où est l'arrêt de matatu ?", "Where is the matatu stop?", "Stage ya matatu iko wapi?"),
        Phrase("Où sont les toilettes ?", "Where are the toilets?", "Choo kiko wapi?", "Choo iko wapi?", null, null, "A1"),
        Phrase("Tout droit", "Straight ahead", "Nenda moja kwa moja", "Enda straight"),
        Phrase("À gauche", "To the left", "Kushoto", null, null, null, "A1", true),
        Phrase("À droite", "To the right", "Kulia", null, null, null, "A1"),
        Phrase("Au coin de la rue", "On the street corner", "Kwenye kona ya barabara"),
        Phrase("En face de", "Opposite / facing", "Kinyume na"),
        Phrase("À côté de", "Next to", "Karibu na", "Jirani na", null, null, "A2"),
        Phrase("Entre… et…", "Between… and…", "Kati ya… na…"),
        Phrase("C'est loin ?", "Is it far?", "Ni mbali?", null, null, null, "A1"),
        Phrase("C'est à combien de minutes à pied ?", "How many minutes on foot?", "Ni dakika ngapi kwa miguu?"),
        Phrase("Je cherche un taxi", "I'm looking for a taxi", "Ninatafuta teksi", "Natafuta cab"),
        Phrase("Un billet, s'il vous plaît", "A ticket, please", "Tiketi tafadhali", null, null, null, "A1"),
        Phrase("Deux billets, s'il vous plaît", "Two tickets, please", "Tiketi mbili tafadhali"),
        Phrase("Le centre-ville", "Downtown / town centre", "Mjini", "Town"),
        Phrase("La banlieue", "The suburbs", "Nje ya mji", null, "Estate = banlieue. Kayole, Umoja, Ruaka — same concept."),
        Phrase("Le carrefour", "The intersection", "Makutano"),
        Phrase("Le feu rouge", "The red light", "Taa nyekundu", "Robo"),
        Phrase("Traversez la rue", "Cross the street", "Vuka barabara"),
        Phrase("Attention aux voitures !", "Watch out for cars!", "Angalia magari!", null, null, "A2"),
        Phrase("C'est par là", "It's that way", "Ni huko", "Ni huko"),
        Phrase("Suivez-moi", "Follow me", "Nifuate", "Nifuate"),
        Phrase("On est arrivés !", "We've arrived!", "Tumefika!", "Tumefika!", null, null, "A1"),
        Phrase("Bienvenue à Nairobi !", "Welcome to Nairobi!", "Karibu Nairobi!")
    ), "u3", "Nairobi runs on landmarks, not street names — 'hapo kwa Total', 'opposite Naivas'. French cities work the same: navigate by boulangerie, pharmacie, église, and you'll never be lost."),
    Lesson("numbers", "🔢", "Les nombres", "Numbers 0 to 100", "Namba", listOf(
        Phrase("Zéro", "Zero", "Sifuri", null, null, null, "A1", true),
        Phrase("Un", "One", "Moja", null, "Un (m) / une (f) — the only number with gender.", null, "A1", true),
        Phrase("Deux", "Two", "Mbili", null),
        Phrase("Trois", "Three", "Tatu", null),
        Phrase("Quatre", "Four", "Nne", null),
        Phrase("Cinq", "Five", "Tano", null),
        Phrase("Six", "Six", "Sita", null, "Pronounced 'seess' — the x sounds!"),
        Phrase("Sept", "Seven", "Saba", null),
        Phrase("Huit", "Eight", "Nane", null, "The h is silent and the t sounds: 'wheet'."),
        Phrase("Neuf", "Nine", "Tisa", null),
        Phrase("Dix", "Ten", "Kumi", null, null, null, "A1", true),
        Phrase("Onze", "Eleven", "Kumi na moja", null),
        Phrase("Douze", "Twelve", "Kumi na mbili", null),
        Phrase("Treize", "Thirteen", "Kumi na tatu", null),
        Phrase("Quatorze", "Fourteen", "Kumi na nne", null),
        Phrase("Quinze", "Fifteen", "Kumi na tano", null),
        Phrase("Seize", "Sixteen", "Kumi na sita", null),
        Phrase("Dix-sept", "Seventeen", "Kumi na saba", null, "17–19 are built like 'ten-seven'."),
        Phrase("Dix-huit", "Eighteen", "Kumi na nane", null),
        Phrase("Dix-neuf", "Nineteen", "Kumi na tisa", null),
        Phrase("Vingt", "Twenty", "Ishirini", null, null, "A1"),
        Phrase("Trente", "Thirty", "Thelathini", null),
        Phrase("Quarante", "Forty", "Arobaini", null),
        Phrase("Cinquante", "Fifty", "Hamsini", null),
        Phrase("Soixante", "Sixty", "Sitini", null),
        Phrase("Soixante-dix", "Seventy", "Sabini", null, "Literally 'sixty-ten' — French counts oddly here."),
        Phrase("Quatre-vingts", "Eighty", "Themanini", null, "Literally 'four-twenties'. The s drops in 81–89."),
        Phrase("Quatre-vingt-dix", "Ninety", "Tisaini", null, "Literally 'four-twenty-ten'. Worth the headache."),
        Phrase("Cent", "A hundred", "Mia moja", null, null, "A1"),
        Phrase("C'est combien ?", "How much is it?", "Ni ngapi?", "Ni how much?", null, null, "A1", true),
        Phrase("Mon numéro est le zéro sept…", "My number is 07…", "Nambari yangu ni sifuri saba…", null, "Practise your real phone number digit by digit.")
    ), "u1"),
    Lesson("shop", "🛍️", "Faire les courses", "Shopping", "Kununua", listOf(
        Phrase("Combien ça coûte ?", "How much is it?", "Hii ni bei gani?", "Ni how much?", null, null, "A1", true),
        Phrase("C'est trop cher", "It's too expensive", "Ni ghali sana", null, null, null, "A1", true),
        Phrase("C'est combien le kilo ?", "How much per kilo?", "Kilo ni ngapi?"),
        Phrase("Vous avez moins cher ?", "Do you have something cheaper?", "Una ya bei nafuu?", "Una ya cheap?"),
        Phrase("Faites-moi un bon prix", "Give me a good price", "Nipunguzie bei", "Nishushie", "The magic market sentence — smile when you say it."),
        Phrase("C'est mon dernier prix", "That's my last price", "Hii ndiyo bei ya mwisho", null, "What the seller says — your cue to decide."),
        Phrase("Je prends ça", "I'll take it", "Nitachukua hii", "Nachukua hii"),
        Phrase("Je prends deux kilos", "I'll take two kilos", "Nitachukua kilo mbili"),
        Phrase("Je peux payer par mobile ?", "Can I pay by mobile?", "Naweza kulipa kwa simu?", "Naweza lipa na M-Pesa?"),
        Phrase("Vous acceptez M-Pesa ?", "Do you take M-Pesa?", "Unakubali M-Pesa?", null, null, "A1"),
        Phrase("Je n'ai que ça sur moi", "That's all I have on me", "Hii tu ndiyo niliyo nayo", null, "Bargaining body language in words."),
        Phrase("L'argent", "Money", "Pesa", "Chapaa", null, null, "A1", true),
        Phrase("La monnaie", "Change (coins)", "Chenchi"),
        Phrase("C'est gratuit ?", "Is it free?", "Ni bure?", "Ni free?"),
        Phrase("Le marché", "The market", "Soko", "Marikiti"),
        Phrase("Le vendeur", "The seller", "Muuzaji", "Muuzaji"),
        Phrase("Le client", "The customer", "Mteja", "Mteja"),
        Phrase("C'est frais ?", "Is it fresh?", "Ni freshi?", "Ni fresh?"),
        Phrase("Je regarde seulement", "I'm just looking", "Naangalia tu", "Naangalia tu", "Buys you browsing peace."),
        Phrase("Je reviendrai demain", "I'll come back tomorrow", "Nitarudi kesho", null, null, "A2"),
        Phrase("Mettez-moi ça de côté", "Keep that aside for me", "Niwekee hii kando"),
        Phrase("Un sac, s'il vous plaît", "A bag, please", "Mfuko tafadhali", "Paper bag tafadhali"),
        Phrase("Le reçu", "The receipt", "Risiti"),
        Phrase("Bonne journée et merci !", "Have a good day, thanks!", "Siku njema, asante!"),
        Phrase("À la prochaine !", "Until next time!", "Tutaonana!", "Tuonane!")
    ), "u3", "Bargaining is expected in markets across Kenya and Francophone Africa alike — in Dakar's Sandaga or Abidjan's Treichville, offering half and meeting in the middle is the dance. Fixed-price supermarkets are the exception, not the rule."),
    Lesson("feel", "😊", "Les émotions", "Feelings", "Hisia", listOf(
        Phrase("Je suis content(e)", "I'm happy", "Nina furaha", "Niko poa sana", null, null, "A1", true),
        Phrase("Je suis très heureux", "I'm very happy", "Nina furaha sana"),
        Phrase("Je suis fatigué(e)", "I'm tired", "Nimechoka", null, null, null, "A1"),
        Phrase("Je suis épuisé", "I'm exhausted", "Nimechoka kabisa", "Nimechoka design"),
        Phrase("Je suis stressé(e)", "I'm stressed", "Nina msongo wa mawazo", "Nina stress"),
        Phrase("Je suis calme", "I'm calm", "Nimetulia"),
        Phrase("J'ai peur", "I'm scared", "Ninaogopa", null, "Peur uses avoir: 'I have fear'.", "A1"),
        Phrase("Je m'ennuie", "I'm bored", "Nimechoshwa", "Nimebore"),
        Phrase("Je suis excité", "I'm excited", "Nina hamu", "Nina hamu", "False friend alert: NOT 'excited' romantically — just enthusiastic!"),
        Phrase("Je suis fier", "I'm proud", "Najivunia"),
        Phrase("Je suis déçu", "I'm disappointed", "Nimevunjika moyo"),
        Phrase("Je suis en colère", "I'm angry", "Nina hasira", "Nina hasira"),
        Phrase("Ça m'énerve", "That annoys me", "Inanikera"),
        Phrase("Je suis désolé(e)", "I'm sorry (heartfelt)", "Pole sana", null, null, null, "A1"),
        Phrase("C'est génial !", "It's awesome!", "Ni nzuri sana!", "Ni kali!", null, null, "A1", true),
        Phrase("Je me sens bien", "I feel good", "Najisikia vizuri", null, "Se sentir = to feel (reflexive).", "A2"),
        Phrase("Je me sens mal", "I feel bad", "Najisikia vibaya"),
        Phrase("J'ai le moral", "I'm in good spirits", "Nina morali"),
        Phrase("Je n'ai pas le moral", "I'm feeling down", "Sina morali", "Sina morale"),
        Phrase("Ça me rend heureux", "That makes me happy", "Hiyo inanifurahisha"),
        Phrase("Tout ira bien", "Everything will be fine", "Kila kitu kitakuwa sawa"),
        Phrase("Courage !", "Hang in there!", "Jikaze!", "Jikaze!", "What the French say instead of 'good luck'.")
    ), "u2"),
    Lesson("family", "👨‍👩‍👧", "La famille", "Family", "Familia", listOf(
        Phrase("Ma mère", "My mother", "Mama yangu", "Mamangu", null, "Ma/mes = feminine words.", "A1", true),
        Phrase("Mon père", "My father", "Baba yangu", "Mzae wangu", null, "Mon/mes = masculine words — even 'mon amie' (my friend, f) to avoid vowel clash!", "A1", true),
        Phrase("Mon frère", "My brother", "Kaka yangu", "Bro wangu"),
        Phrase("Ma sœur", "My sister", "Dada yangu", "Sis wangu"),
        Phrase("Mes parents", "My parents", "Wazazi wangu"),
        Phrase("Mon fils", "My son", "Mwanangu wa kiume"),
        Phrase("Ma fille", "My daughter", "Mwanangu wa kike", null, "Fille also means 'girl' generally."),
        Phrase("Mon mari", "My husband", "Mume wangu"),
        Phrase("Ma femme", "My wife", "Mke wangu", null, "Also means 'woman' — context decides."),
        Phrase("Mon grand-père", "My grandfather", "Babu yangu"),
        Phrase("Ma grand-mère", "My grandmother", "Bibi yangu"),
        Phrase("Mon oncle", "My uncle", "Mjomba / Ami yangu", null, "French has one word; Swahili distinguishes mjomba (maternal) vs ami (paternal)."),
        Phrase("Ma tante", "My aunt", "Shangazi / Mama mdogo", null, "Same story: shangazi vs mama mdogo collapse into tante."),
        Phrase("Mon cousin", "My (male) cousin", "Binamu yangu"),
        Phrase("Ma cousine", "My (female) cousin", "Binamu yangu", null, "Cousin/cousine change with gender, unlike binamu."),
        Phrase("Mes amis", "My friends", "Rafiki zangu", "Mabeshte zangu"),
        Phrase("Mon meilleur ami", "My best friend", "Rafiki yangu wa dhati", "Beshte yangu"),
        Phrase("Nous sommes une grande famille", "We are a big family", "Sisi ni familia kubwa", "Sisi ni familia kubwa"),
        Phrase("J'ai deux sœurs", "I have two sisters", "Nina dada wawili", "Niko na madaa wawili"),
        Phrase("Je suis fils unique", "I'm an only child (m)", "Mimi ni mtoto wa pekee"),
        Phrase("Je suis fille unique", "I'm an only child (f)", "Mimi ni mtoto wa pekee"),
        Phrase("Toute la famille", "The whole family", "Familia yote"),
        Phrase("À la maison", "At home", "Nyumbani", null, null, "A1"),
        Phrase("Mon bébé", "My baby", "Mtoto wangu mchanga"),
        Phrase("Félicitations pour le bébé !", "Congrats on the baby!", "Hongera kwa mtoto!")
    ), "u2", "Family is everything in Kenya and across Francophone Africa — expect aunties in Dakar or Kinshasa to adopt you on day one and ask when you're bringing the whole family to visit."),
    Lesson("time", "⏰", "Le temps", "Time", "Wakati", listOf(
        Phrase("Quelle heure est-il ?", "What time is it?", "Ni saa ngapi?", "Ni saa ngapi?", null, null, "A1", true),
        Phrase("Il est huit heures", "It is 8 o'clock", "Ni saa mbili asubuhi", "Ni saa mbili", "Swahili time starts at 6am, so 8am is 'saa mbili' — French works like English here."),
        Phrase("Il est midi", "It's noon", "Ni saa sita mchana", null, "Midi = noon, minuit = midnight. Meal o'clock!"),
        Phrase("Il est minuit", "It's midnight", "Ni saa sita usiku"),
        Phrase("Il est huit heures et demie", "It's half past eight", "Ni saa mbili na nusu", null, "Et demie = and a half."),
        Phrase("Il est neuf heures moins le quart", "It's quarter to nine", "Ni saa tatu kasorobo", null, "Moins le quart = minus a quarter."),
        Phrase("Aujourd'hui", "Today", "Leo", "Leo", null, null, "A1", true),
        Phrase("Demain", "Tomorrow", "Kesho", "Kesho"),
        Phrase("Après-demain", "The day after tomorrow", "Kesho kutwa"),
        Phrase("Hier", "Yesterday", "Jana", "Jana"),
        Phrase("Avant-hier", "The day before yesterday", "Juzi"),
        Phrase("Le matin", "The morning", "Asubuhi", "Asubuhi"),
        Phrase("L'après-midi", "The afternoon", "Mchana"),
        Phrase("Le soir", "The evening", "Jioni", "Jioni"),
        Phrase("La nuit", "The night", "Usiku"),
        Phrase("Lundi", "Monday", "Jumatatu"),
        Phrase("Mardi", "Tuesday", "Jumanne"),
        Phrase("Mercredi", "Wednesday", "Jumatano"),
        Phrase("Jeudi", "Thursday", "Alhamisi"),
        Phrase("Vendredi", "Friday", "Ijumaa"),
        Phrase("Samedi", "Saturday", "Jumamosi", null, null, "A1"),
        Phrase("Dimanche", "Sunday", "Jumapili", null, "Jumapili church lunches — say it and every Kenyan nods."),
        Phrase("Le week-end", "The weekend", "Wikendi"),
        Phrase("La semaine prochaine", "Next week", "Wiki ijayo", null, null, "A2"),
        Phrase("Je suis en retard", "I am late", "Nimechelewa", "Nimechelewa", null, null, "A1"),
        Phrase("Je suis à l'heure", "I'm on time", "Niko kwa wakati", "Niko on time")
    ), "u2"),
    Lesson("weather", "🌦️", "La météo", "Weather", "Hali ya hewa", listOf(
        Phrase("Il fait beau", "The weather is nice", "Hali ya hewa ni nzuri", "Kuna jua poa", null, "Weather uses 'il fait' + adjective.", "A1"),
        Phrase("Il pleut", "It is raining", "Mvua inanyesha", "Mvua inanesa", null, null, "A1", true),
        Phrase("Il pleut des cordes", "It's pouring (raining ropes)", "Mvua inanyesha sana", "Mvua inanesa mob", "The French love dramatic rain idioms."),
        Phrase("Il fait chaud", "It is hot", "Kuna joto", "Kuna joto", null, null, "A1"),
        Phrase("Il fait très chaud aujourd'hui", "It's very hot today", "Leo kuna joto sana"),
        Phrase("Il fait froid", "It is cold", "Kuna baridi", "Kuna baridi"),
        Phrase("Il fait frais", "It's cool/chilly", "Kuna ubaridi kidogo", null, "Frais = pleasantly cool; froid = properly cold."),
        Phrase("Il neige", "It's snowing", "Theluji inaanguka", null, "Rare in Kenya, common in Paris in January!"),
        Phrase("Il y a du soleil", "It is sunny", "Kuna jua", "Kuna jua kali", null, null, "A1"),
        Phrase("Il y a des nuages", "It's cloudy", "Kuna mawingu"),
        Phrase("Il y a du vent", "It's windy", "Kuna upepo"),
        Phrase("Il y a de l'orage", "There's a storm", "Kuna dhoruba"),
        Phrase("Quel temps fait-il ?", "How is the weather?", "Hali ya hewa ikoje?", "Hewa iko aje?", null, null, "A1", true),
        Phrase("Quel temps fera-t-il demain ?", "What will the weather be tomorrow?", "Hali ya hewa ya kesho ikoje?", null, null, "B1"),
        Phrase("J'ai chaud", "I'm hot", "Nina joto", null, "Careful: 'je suis chaud' means something else entirely — always 'j'ai chaud'!", "A1"),
        Phrase("J'ai froid", "I'm cold", "Nina baridi", null, "Same rule: avoir, never être, for hot/cold.", "A1"),
        Phrase("Prends un parapluie", "Take an umbrella", "Chukua mwavuli"),
        Phrase("Mets une veste", "Put on a jacket", "Vaa koti"),
        Phrase("La saison des pluies", "The rainy season", "Msimu wa mvua", "Msimu wa mvua", "Long rains, short rains — Nairobi runs on them."),
        Phrase("La saison sèche", "The dry season", "Msimu wa kiangazi"),
        Phrase("Il fait un temps de chien", "The weather is awful", "Hali ya hewa ni mbaya", null, "Literally 'dog weather' — dramatic and fun."),
        Phrase("Après la pluie, le beau temps", "After rain comes sunshine", "Baada ya mvua, jua", null, "A French proverb for hard weeks."),
        Phrase("Quelle belle journée !", "What a beautiful day!", "Siku nzuri!", null, null, "A1"),
        Phrase("On reste à la maison", "We're staying home", "Tutakaa nyumbani"),
        Phrase("C'est le moment de sortir", "It's time to go out", "Ni wakati wa kutoka")
    ), "u2"),
    Lesson("school", "🎒", "À l'école", "School", "Shuleni", listOf(
        Phrase("Je vais à l'université", "I go to university", "Ninaenda chuo kikuu", "Naenda campo", null, null, "A1", true),
        Phrase("J'étudie le français", "I study French", "Ninasoma Kifaransa", "Nasoma French"),
        Phrase("J'étudie l'ingénierie électrique", "I study electrical engineering", "Ninasomea uhandisi wa umeme", "Nasoma electrical"),
        Phrase("Je suis en première année", "I'm a first year", "Niko mwaka wa kwanza", "Niko first year"),
        Phrase("Le professeur", "The teacher (m)", "Mwalimu", "Mwalimu", null, null, "A1"),
        Phrase("La professeure", "The teacher (f)", "Mwalimu", null, "Both forms exist — use la professeure for women."),
        Phrase("Le livre", "The book", "Kitabu", "Buku"),
        Phrase("Le cahier", "The notebook", "Daftari"),
        Phrase("Le stylo", "The pen", "Kalamu"),
        Phrase("Je fais mes devoirs", "I do my homework", "Ninafanya homework yangu", "Nafanya assignment"),
        Phrase("J'ai un examen demain", "I have an exam tomorrow", "Nina mtihani kesho", "Niko na exam kesho", null, null, "A1", true),
        Phrase("J'ai réussi mon examen", "I passed my exam", "Nimefaulu mtihani", "Nimepass exam"),
        Phrase("J'ai raté mon examen", "I failed my exam", "Nimefeli mtihani", "Nimefail exam", "Rater = to fail AND to miss — context decides."),
        Phrase("Bonne chance !", "Good luck!", "Kila la kheri!", "Good luck!"),
        Phrase("Je fais un stage", "I'm doing an internship", "Ninafanya mafunzo kazini", "Nafanya attachment", null, null, "A2"),
        Phrase("Je cherche un stage", "I'm looking for an internship", "Ninatafuta mafunzo kazini"),
        Phrase("Je veux devenir ingénieur", "I want to become an engineer", "Nataka kuwa mhandisi"),
        Phrase("La bibliothèque", "The library", "Maktaba", "Library"),
        Phrase("La salle de classe", "The classroom", "Darasa"),
        Phrase("Le campus", "The campus", "Chuo", "Campo"),
        Phrase("Mon camarade de classe", "My classmate", "Mwanafunzi mwenzangu"),
        Phrase("Le directeur", "The principal / director", "Mkuu"),
        Phrase("Les frais de scolarité", "School fees", "Karo", "School fees"),
        Phrase("Je n'ai pas le temps", "I don't have time", "Sina muda", null, null, "A2"),
        Phrase("Les vacances", "The holidays", "Likizo", "Holiday", "Always plural in French!")
    ), "u3", "Campus French opens real doors: exchange semesters in Dakar or Lyon, Alliance Française scholarships, and internships at Francophone firms in Nairobi all start with 'je suis étudiant'."),
    Lesson("hobbies", "⚽", "Les loisirs", "Hobbies", "Burudani", listOf(
        Phrase("J'aime le football", "I like football", "Ninapenda mpira", "Napenda ball", null, null, "A1", true),
        Phrase("Je joue au foot", "I play football", "Ninacheza mpira", "Nacheza ball", "Jouer à + sport.", "A1"),
        Phrase("Je joue au basket", "I play basketball", "Ninacheza basket", "Nacheza basket"),
        Phrase("Tu supportes quelle équipe ?", "Which team do you support?", "Unashabikia timu gani?", "Team gani?"),
        Phrase("Je supporte Gor Mahia", "I support Gor Mahia", "Ninashabikia Gor", "Mimi ni K'Ogalo", "Swap in AFC, Tusker, or your European club."),
        Phrase("Quel est ton sport préféré ?", "What's your favourite sport?", "Mchezo unaoupenda ni upi?"),
        Phrase("J'écoute de la musique", "I listen to music", "Ninasikiliza muziki", "Naskiza ngoma"),
        Phrase("J'écoute du gengetone", "I listen to gengetone", "Ninasikiliza gengetone", null, "Kenyan sound — great conversation topic with young French speakers."),
        Phrase("Je joue de la guitare", "I play guitar", "Ninapiga gitaa", null, "Jouer DE + instrument, jouer À + sport. Classic trap.", "A2"),
        Phrase("Je regarde des films", "I watch movies", "Ninaangalia filamu", "Na watch movie"),
        Phrase("Je regarde des séries", "I watch series", "Ninaangalia series", "Na watch series"),
        Phrase("Tu as vu le dernier match ?", "Did you see the last match?", "Uliangalia mechi ya mwisho?"),
        Phrase("J'aime voyager", "I like to travel", "Napenda kusafiri", null, null, "A1"),
        Phrase("J'aime cuisiner", "I like cooking", "Napenda kupika"),
        Phrase("J'aime lire", "I like reading", "Napenda kusoma"),
        Phrase("Je fais du sport", "I do sports", "Ninafanya mazoezi"),
        Phrase("Je cours le matin", "I run in the morning", "Ninakimbia asubuhi"),
        Phrase("Je nage", "I swim", "Ninaogelea"),
        Phrase("Le samedi, je me repose", "On Saturdays I rest", "Jumamosi napumzika", "Jumamosi nachill"),
        Phrase("Le dimanche, je vais à l'église", "On Sundays I go to church", "Jumapili naenda kanisani"),
        Phrase("Tu veux jouer avec moi ?", "Do you want to play with me?", "Unataka kucheza nami?"),
        Phrase("On fait un match samedi ?", "A match on Saturday?", "Tuna mechi Jumamosi?"),
        Phrase("J'ai gagné !", "I won!", "Nimeshinda!", null, null, "A1"),
        Phrase("Bien joué !", "Well played!", "Umeplay poa!", "Poa sana!"),
        Phrase("La prochaine fois !", "Next time!", "Wakati ujao!")
    ), "u3"),
    Lesson("phone", "📱", "Le téléphone", "Phone", "Simu", listOf(
        Phrase("Je t'appelle ce soir", "I'll call you tonight", "Nitakupigia leo jioni", "Nitakucall leo", null, null, "A1"),
        Phrase("Tu peux me rappeler ?", "Can you call me back?", "Unaweza kunipigia tena?", "Nipigie tena?"),
        Phrase("Envoie-moi un message", "Send me a message", "Nitumie ujumbe", "Nitumia message", "Imperative: texting a friend."),
        Phrase("Envoie-moi ta position", "Send me your location", "Nitumia location yako", "Nitumia pin"),
        Phrase("Mon téléphone est déchargé", "My phone is dead", "Simu yangu imeisha moto", "Simu yangu imezima"),
        Phrase("Mon téléphone ne charge plus", "My phone won't charge anymore", "Simu yangu haichaji tena"),
        Phrase("Tu as WhatsApp ?", "Do you have WhatsApp?", "Una WhatsApp?", "Uko na WhatsApp?", null, null, "A1", true),
        Phrase("Ajoute-moi sur WhatsApp", "Add me on WhatsApp", "Niongeze WhatsApp"),
        Phrase("Il y a du wifi ?", "Is there wifi?", "Kuna wifi?"),
        Phrase("C'est quoi le mot de passe du wifi ?", "What's the wifi password?", "Password ya wifi ni ipi?"),
        Phrase("Je n'ai pas de réseau", "I have no signal", "Sina mtandao", "Sina network"),
        Phrase("Je n'ai plus de crédit", "I'm out of airtime", "Sina bando", "Sina credit"),
        Phrase("Je charge mon téléphone", "I charge my phone", "Ninachaji simu yangu", "Nachaji simu"),
        Phrase("Passe-moi ton chargeur", "Lend me your charger", "Niazime chaja yako", "Nipe chaja"),
        Phrase("Mon écran est cassé", "My screen is cracked", "Kioo changu kimevunjika"),
        Phrase("Je t'appelle en vidéo", "I'll video-call you", "Nitakupigia video", "Nitakupigia video call"),
        Phrase("Décroche !", "Pick up!", "Shika simu!", "Shika!"),
        Phrase("Raccroche, je te rappelle", "Hang up, I'll call back", "Kata simu, nitakupigia"),
        Phrase("Ça coupe tout le temps", "It keeps cutting out", "Inakatika mara kwa mara"),
        Phrase("Je t'entends mal", "I hear you badly", "Nakusikia vibaya"),
        Phrase("Peux-tu répéter ?", "Can you repeat?", "Unaweza kurudia?", null, null, "A1"),
        Phrase("Envoie-moi la photo", "Send me the photo", "Nitumia picha"),
        Phrase("Je suis en ligne", "I'm online", "Niko online"),
        Phrase("Je t'envoie le lien", "I'll send you the link", "Nitakutumia link"),
        Phrase("À tout à l'heure au téléphone", "Talk soon on the phone", "Tutaongea kwa simu")
    ), "u3"),
    Lesson("health", "🏥", "La santé", "Health", "Afya", listOf(
        Phrase("Je suis malade", "I am sick", "Mimi ni mgonjwa", "Mimi ni mgonjwa", null, null, "A1", true),
        Phrase("Je ne me sens pas bien", "I don't feel well", "Sijisikii vizuri"),
        Phrase("J'ai mal à la tête", "I have a headache", "Ninaumwa na kichwa", "Kichwa inanuma", "Avoir mal à + body part.", "A1"),
        Phrase("J'ai mal au ventre", "My stomach hurts", "Tumbo linauma"),
        Phrase("J'ai mal à la gorge", "My throat hurts", "Koo linauma"),
        Phrase("J'ai de la fièvre", "I have a fever", "Nina homa", null, null, "A1"),
        Phrase("Je tousse", "I'm coughing", "Ninakohoa"),
        Phrase("J'ai le rhume", "I have a cold", "Nina mafua"),
        Phrase("Je suis allergique", "I'm allergic", "Nina aleji", null, null, "A2"),
        Phrase("J'ai besoin d'un médecin", "I need a doctor", "Ninahitaji daktari", null, null, null, "A1", true),
        Phrase("Où est la pharmacie ?", "Where is the pharmacy?", "Famasi iko wapi?", null, null, "A1"),
        Phrase("Où est l'hôpital ?", "Where is the hospital?", "Hospitali iko wapi?"),
        Phrase("Je vais à l'hôpital", "I go to the hospital", "Ninaenda hospitali", "Naenda hosi"),
        Phrase("Appelez une ambulance !", "Call an ambulance!", "Pigia ambulensi!", null, null, "A1"),
        Phrase("J'ai une ordonnance", "I have a prescription", "Nina cheti cha daktari"),
        Phrase("Deux comprimés par jour", "Two pills a day", "Vidonge viwili kwa siku"),
        Phrase("Avant les repas", "Before meals", "Kabla ya chakula"),
        Phrase("Ça fait mal ici", "It hurts here", "Inauma hapa"),
        Phrase("Ça va mieux", "It is better now", "Sasa ni afadhali", "Sasa niko poa"),
        Phrase("Je vais mieux, merci", "I'm better, thanks", "Naendelea vizuri, asante"),
        Phrase("Repose-toi bien", "Rest well", "Pumzika vizuri", "Pumzika poa", "Tu-imperative of se reposer."),
        Phrase("Bois beaucoup d'eau", "Drink lots of water", "Kunywa maji mengi"),
        Phrase("Prompt rétablissement !", "Get well soon!", "Pona haraka!", "Get well soon!")
    ), "u4"),
    Lesson("travel", "✈️", "Voyager", "Travel", "Kusafiri", listOf(
        Phrase("Je veux aller à Paris", "I want to go to Paris", "Ninataka kwenda Paris", "Nataka kwenda Paris"),
        Phrase("Je veux visiter Dakar", "I want to visit Dakar", "Ninataka kutembelea Dakar", null, "Dakar, Abidjan, Kigali — closer Francophone adventures."),
        Phrase("Le passeport", "The passport", "Pasipoti", "Passport", null, null, "A1", true),
        Phrase("Le visa", "The visa", "Visa"),
        Phrase("Je prends le matatu", "I take the matatu", "Ninapanda matatu", "Napanda matatu", "Kenyan touch: matatu = minibus."),
        Phrase("Je prends l'avion", "I take the plane", "Ninapanda ndege"),
        Phrase("Bon voyage !", "Have a good trip!", "Safari njema!", "Safari njema!", null, null, "A1", true),
        Phrase("Je réserve un billet", "I book a ticket", "Ninakata tiketi", "Nabook tiketi"),
        Phrase("Je réserve une chambre", "I book a room", "Ninaweka chumba", "Nabook room"),
        Phrase("J'ai une réservation", "I have a reservation", "Nina booking", "Niko na booking"),
        Phrase("L'aéroport", "The airport", "Uwanja wa ndege", "Airport", null, null, "A1"),
        Phrase("La gare", "The station", "Stesheni"),
        Phrase("Mon vol est retardé", "My flight is delayed", "Ndege yangu imechelewa"),
        Phrase("Mon vol est annulé", "My flight is cancelled", "Ndege yangu imefutwa"),
        Phrase("Où est la sortie ?", "Where is the exit?", "Kutokea ni wapi?"),
        Phrase("Voici mon billet", "Here is my ticket", "Hii hapa tiketi yangu"),
        Phrase("Voici mon passeport", "Here is my passport", "Hii hapa pasipoti yangu"),
        Phrase("C'est combien la nuit ?", "How much per night?", "Usiku ni ngapi?"),
        Phrase("Le petit-déjeuner est inclus ?", "Is breakfast included?", "Kifungua kinywa kimejumuishwa?"),
        Phrase("À quelle heure faut-il partir ?", "What time must we leave?", "Tunatakiwa kuondoka saa ngapi?"),
        Phrase("J'ai fait ma valise", "I've packed my bag", "Nimefunga begi"),
        Phrase("J'ai oublié mon chargeur", "I forgot my charger", "Nimesahau chaja"),
        Phrase("Bonnes vacances !", "Happy holidays!", "Likizo njema!"),
        Phrase("Raconte-moi ton voyage !", "Tell me about your trip!", "Niambie kuhusu safari yako!"),
        Phrase("Bienvenue chez nous !", "Welcome to our place!", "Karibu kwetu!")
    ), "u4"),
    Lesson("opinions", "💭", "Donner son avis", "Opinions", "Maoni", listOf(
        Phrase("Je pense que c'est bien", "I think it's good", "Ninafikiri ni vizuri", "Nadai ni poa", null, "Penser que + indicative.", "A2"),
        Phrase("Je pense que non", "I don't think so", "Sidhani hivyo"),
        Phrase("À mon avis", "In my opinion", "Kwa maoni yangu", "Kwa view yangu", null, null, "A2", true),
        Phrase("Selon moi", "According to me", "Kwa mujibu wangu"),
        Phrase("Je suis d'accord", "I agree", "Ninakubali", "Niko nawe", null, null, "A1", true),
        Phrase("Je suis tout à fait d'accord", "I completely agree", "Nakubali kabisa"),
        Phrase("Je ne suis pas d'accord", "I disagree", "Sikubali", "Siko nawe"),
        Phrase("Pas vraiment", "Not really", "Sio sana", "Sio hivyo", "Soft disagreement — very French."),
        Phrase("C'est intéressant", "It's interesting", "Inavutia", "Ni interesting"),
        Phrase("C'est bizarre", "It's weird", "Ni ajabu"),
        Phrase("C'est évident", "It's obvious", "Ni wazi"),
        Phrase("C'est important", "It's important", "Ni muhimu"),
        Phrase("Peut-être", "Maybe", "Labda", "Labda", null, null, "A1"),
        Phrase("Bien sûr", "Of course", "Bila shaka", "Obviously", null, null, "A2"),
        Phrase("Pourquoi pas ?", "Why not?", "Kwa nini isiwe?", "Mbona isiwe?"),
        Phrase("Tu as raison", "You're right", "Uko sahihi", "Uko right", null, null, "A2"),
        Phrase("Tu as tort", "You're wrong", "Umekosea", "Umekosea", "Said with a smile among friends only!"),
        Phrase("Je vois", "I see", "Naelewa", "Naelewa"),
        Phrase("Je comprends", "I understand", "Naelewa", null, null, "A1"),
        Phrase("Explique-moi", "Explain to me", "Niexplainie", "Niexplainie"),
        Phrase("Donne-moi un exemple", "Give me an example", "Nipe mfano"),
        Phrase("Franchement, je ne sais pas", "Honestly, I don't know", "Ukweli sijui"),
        Phrase("Ça dépend", "It depends", "Inategemea"),
        Phrase("En fait", "Actually / in fact", "Kwa kweli", "Kumbe", "Great filler word while you think."),
        Phrase("Bref", "Anyway / in short", "Kwa ufupi", "Anyway")
    ), "u4"),
    Lesson("verbs", "🔤", "Verbes clés", "Key verbs", "Vitenzi muhimu", listOf(
        Phrase("Être : je suis, tu es", "To be: I am, you are", "Kuwa: mimi ni, wewe ni", null, "Most important verb. Master it first.", null, "A1", true),
        Phrase("Être : il est, nous sommes", "To be: he is, we are", "Kuwa: yeye ni, sisi ni"),
        Phrase("Être : vous êtes, ils sont", "To be: you are, they are", "Kuwa: ninyi ni, wao ni"),
        Phrase("Avoir : j'ai, tu as", "To have: I have, you have", "Kuwa na: nina, una", null, null, null, "A1", true),
        Phrase("Avoir : il a, nous avons", "To have: he has, we have", "Kuwa na: ana, tuna"),
        Phrase("Avoir : vous avez, ils ont", "To have: you have, they have", "Kuwa na: mna, wana"),
        Phrase("Aller : je vais, tu vas", "To go: I go, you go", "Kwenda: ninaenda, unaenda", null, null, null, "A1", true),
        Phrase("Aller : il va, nous allons", "To go: he goes, we go", "Kwenda: anaenda, tunaenda"),
        Phrase("Aller : vous allez, ils vont", "To go: you go, they go", "Kwenda: mnaenda, wanaenda"),
        Phrase("Faire : je fais, tu fais", "To do: I do, you do", "Kufanya: ninafanya, unafanya", null, null, "A1"),
        Phrase("Faire : il fait, nous faisons", "To do: he does, we do", "Kufanya: anafanya, tunafanya"),
        Phrase("Faire : vous faites, ils font", "To do: you do, they do", "Kufanya: mnafanya, wanafanya"),
        Phrase("Parler : je parle, tu parles", "To speak: I speak, you speak", "Kuzungumza: ninazungumza, unazungumza"),
        Phrase("Manger : je mange, nous mangeons", "To eat: I eat, we eat", "Kula: ninakula, tunakula", null, "Nous mangeons keeps the soft g with 'e'."),
        Phrase("Finir : je finis, tu finis", "To finish: I finish, you finish", "Kumaliza: ninamaliza, unamaliza", null, "-ir verbs double the 's' sound: finis, finit.", "A2"),
        Phrase("Prendre : je prends, tu prends", "To take: I take, you take", "Kuchukua: ninachukua, unachukua", null, "Irregular but everywhere: prendre le bus, prendre un café.", "A2"),
        Phrase("Vouloir : je veux, tu veux", "To want: I want, you want", "Kutaka: ninataka, unataka", null, null, "A1"),
        Phrase("Pouvoir : je peux, tu peux", "To be able: I can, you can", "Kuweza: ninaweza, unaweza", null, null, "A2"),
        Phrase("Savoir : je sais", "To know (fact): I know", "Kujua: ninajua", null, "Savoir = facts/skills; connaître = people/places.", "A2"),
        Phrase("Connaître : je connais Nairobi", "To know: I know Nairobi", "Kuijua: ninaijua Nairobi"),
        Phrase("Je veux manger", "I want to eat", "Ninataka kula", "Nataka kula", "Vouloir + infinitive: no 'to' needed.", null, "A1"),
        Phrase("Tu peux m'aider ?", "Can you help me?", "Unaweza kunisaidia?", "Unaweza nisaidia?"),
        Phrase("Je dois partir", "I must go", "Lazima niende", null, "Devoir + infinitive = must.", "A2"),
        Phrase("Il faut étudier", "One must study / gotta study", "Lazima kusoma", null, "Falloir = the general 'must'.", "B1"),
        Phrase("J'aime danser", "I like dancing", "Napenda kucheza", null, "Aimer + infinitive.", "A1")
    ), "u4", "Four verbs run the whole language: être, avoir, aller, faire. If you only drill one screen for a week, make it this one — every tense in the app hangs off these four."),
    Lesson("past", "⏳", "Le passé", "Past tense", "Wakati uliopita", listOf(
        Phrase("J'ai mangé", "I ate", "Nilikula", "Nilikula", "Passé composé = avoir/être + past participle.", "Avoir + mangé.", "A2", true),
        Phrase("J'ai parlé avec mon ami", "I talked with my friend", "Niliongea na rafiki yangu"),
        Phrase("J'ai fini mes devoirs", "I finished my homework", "Nilimaliza homework"),
        Phrase("J'ai vu un bon film", "I saw a good movie", "Niliona movie poa", null, "Voir → vu (irregular participle).", "A2"),
        Phrase("J'ai pris le bus", "I took the bus", "Nilipanda basi", null, "Prendre → pris.", "A2"),
        Phrase("J'ai fait du sport", "I did sports", "Nilifanya mazoezi", null, "Faire → fait.", "A2"),
        Phrase("J'ai eu un problème", "I had a problem", "Nilikuwa na shida", null, "Avoir → eu.", "B1"),
        Phrase("J'ai été malade", "I was sick", "Nilikuwa mgonjwa", null, "Être → été.", "B1"),
        Phrase("Je suis allé(e) à Nairobi", "I went to Nairobi", "Nilikwenda Nairobi", "Nilienda Nairobi", "Aller uses être.", "Aller + être.", "A2", true),
        Phrase("Je suis venu(e) hier", "I came yesterday", "Nilikuja jana", null, "Venir uses être too.", "A2"),
        Phrase("Elle est partie tôt", "She left early", "Aliondoka mapema", null, "Partir uses être; the participle agrees: partie (f).", "B1"),
        Phrase("Nous avons fini", "We finished", "Tulimaliza", "Tulimaliza"),
        Phrase("Nous sommes sortis", "We went out", "Tulitoka nje", null, null, "B1"),
        Phrase("Vous avez aimé ?", "Did you like it?", "Mlipenda?"),
        Phrase("Tu as vu ce film ?", "Did you see this movie?", "Uli watch movie hii?", "Uliiona movie hii?"),
        Phrase("Qu'est-ce que tu as fait ?", "What did you do?", "Ulifanya nini?", null, null, "A2"),
        Phrase("Où es-tu allé ?", "Where did you go?", "Ulikwenda wapi?"),
        Phrase("Hier j'ai beaucoup appris", "Yesterday I learned a lot", "Jana nilijifunza mengi", "Jana nilijifunza mob"),
        Phrase("Je n'ai pas compris", "I didn't understand", "Sikuelewa", null, "Ne…pas wraps the helper: n'ai pas compris.", null, "A2", true),
        Phrase("Je ne suis pas sorti", "I didn't go out", "Sikutoka nje"),
        Phrase("Il a plu toute la journée", "It rained all day", "Mvua ilinyesha siku nzima", null, "Weather verbs use il + avoir.", "B1"),
        Phrase("C'était génial", "It was awesome", "Ilikuwa kali", null, "Être → c'était (imperfect — the storyteller's tense).", "B1"),
        Phrase("Raconte-moi tout !", "Tell me everything!", "Niambie kila kitu!", null, null, "A2"),
        Phrase("Et après ?", "And then?", "Na kisha?", "Na then?", null, null, "A2"),
        Phrase("C'est tout pour hier", "That's all for yesterday", "Hayo tu kwa jana")
    ), "u4", "Movement verbs (aller, venir, partir, sortir, rentrer) take être, everything else takes avoir — learn that one sentence and the past tense is 80% solved. Agreement only matters with être (allé/​allée) or a preceding object."),
)

val UNITS: List<StudyUnit> = listOf(
    StudyUnit("u1", "🌱", "Survie", "Survival French (A1)", "Kiswahili: Kuanza", listOf("greet", "polite", "intro", "numbers", "ask", "alphabet", "farewell", "titles", "sounds")),
    StudyUnit("u2", "🏠", "Le quotidien", "Daily life (A1–A2)", "Maisha ya kila siku", listOf("shop", "feel", "family", "time", "weather", "school", "food", "colours", "breakfast", "restaurant", "clothes", "home", "sunday", "music")),
    StudyUnit("u3", "🌆", "Comme un local", "Talk like a local (A2)", "Kama mwenyeji", listOf("city", "hobbies", "phone", "health", "travel", "opinions", "verbs", "past", "weekend", "cooking", "cinema", "exams", "news", "proverbs")),
    StudyUnit("u4", "🧭", "Se débrouiller", "Getting things done (A2–B1)", "Kujitegemea", listOf("directions", "money", "transport", "emergencies", "housing", "bank", "doctor", "salon", "police", "airport", "hotel")),
    StudyUnit("u5", "💬", "Créer des liens", "Connecting (B1)", "Kuwasiliana", listOf("friends", "daystory", "debate", "slang", "roast", "sorry", "flirt", "interview", "meetup", "wedding", "derby")),
    StudyUnit("u6", "🌉", "Pont vers la fluidité", "Fluency bridge (B1)", "Daraja", listOf("reviewa2", "bridgeb1", "b1chat", "b1past", "b1debate", "b1future"))
)

fun lessonById(id: String): Lesson? = allLessons().find { it.id == id }

// Custom lessons created by the user (in-memory + persisted separately in Store)
var customLessonsCache: List<Lesson> = emptyList()
fun allLessons(): List<Lesson> = LESSONS + EXTRA_LESSONS + EXTRA2_LESSONS + customLessonsCache

val ALL_PHRASES: List<Phrase> = (LESSONS + EXTRA_LESSONS + EXTRA2_LESSONS).flatMap { it.phrases }

val VERBS: List<Verb> = listOf(
    Verb("être", "to be", "kuwa", mapOf("je" to "suis", "tu" to "es", "il" to "est", "nous" to "sommes", "vous" to "êtes", "ils" to "sont"), mapOf("je" to "ai été", "tu" to "as été")),
    Verb("avoir", "to have", "kuwa na", mapOf("je" to "ai", "tu" to "as", "il" to "a", "nous" to "avons", "vous" to "avez", "ils" to "ont"), mapOf("je" to "ai eu", "tu" to "as eu")),
    Verb("aller", "to go", "kwenda", mapOf("je" to "vais", "tu" to "vas", "il" to "va", "nous" to "allons", "vous" to "allez", "ils" to "vont")),
    Verb("faire", "to do", "kufanya", mapOf("je" to "fais", "tu" to "fais", "il" to "fait", "nous" to "faisons", "vous" to "faites", "ils" to "font")),
    Verb("parler", "to speak", "kuzungumza", mapOf("je" to "parle", "tu" to "parles", "il" to "parle", "nous" to "parlons", "vous" to "parlez", "ils" to "parlent")),
    Verb("manger", "to eat", "kula", mapOf("je" to "mange", "tu" to "manges", "il" to "mange", "nous" to "mangeons", "vous" to "mangez", "ils" to "mangent"))
)

enum class QType {
    FR_TO_MEANING, MEANING_TO_FR, LISTEN, ORDER, SPEAK, CLOZE, MATCH, TYPE, CONJUGATE,
    // §5: dictation (type what you hear), minimal pairs (confusable sounds),
    // replay (reconstruct an exchange), story (micro-story comprehension)
    DICTATION, MINIMAL_PAIR, REPLAY, STORY
}

data class Question(
    val type: QType,
    val phrase: Phrase,
    val options: List<String> = emptyList(),
    val answer: String = "",
    val words: List<String> = emptyList(),
    // cloze
    val clozeDisplay: String = "",
    val clozeMissing: String = "",
    // match: pairs of fr<->meaning, prompt list
    val matchPairs: List<Pair<String, String>> = emptyList(),
    // conjugate
    val verb: String = "",
    val pronoun: String = "",
    val hint: String = "",
    // story (§5.12): micro-story text + its title
    val storyText: String = "",
    val storyQuestion: String = "",
    // replay (§5.11): exchange lines in correct order
    val replayLines: List<String> = emptyList()
)

fun phraseKey(p: Phrase): String = p.key()

fun makeCloze(ph: Phrase): Pair<String, String> {
    val tokens = ph.fr.split(" ").filter { it.isNotBlank() }
    if (tokens.size < 2) return ph.fr to ""
    // pick longest word > 3 chars, else middle token
    val idx = tokens.indices.filter { tokens[it].trim { c -> !c.isLetter() }.length >= 3 }
        .maxByOrNull { tokens[it].length } ?: (tokens.size / 2)
    val missing = tokens[idx]
    val display = tokens.mapIndexed { i, t -> if (i == idx) "____" else t }.joinToString(" ")
    return display to missing.trim { c -> !c.isLetterOrDigit() && c != '\'' }
}

fun buildQuestions(lesson: Lesson, lang: HelpLang, count: Int = 12, due: List<Phrase> = emptyList()): List<Question> {
    val base = (due + lesson.phrases.shuffled()).distinctBy { it.fr }.take(maxOf(count, lesson.phrases.size)).ifEmpty { lesson.phrases.shuffled() }
    // Sequencing (§5): no two consecutive questions share a type; recognition first, production later
    val order = listOf(
        QType.FR_TO_MEANING, QType.LISTEN, QType.MEANING_TO_FR,
        QType.CLOZE, QType.ORDER, QType.MATCH,
        QType.TYPE, QType.DICTATION, QType.MINIMAL_PAIR,
        QType.REPLAY, QType.CONJUGATE, QType.STORY, QType.SPEAK
    )
    return (0 until count).map { i ->
        val ph = base[i % base.size]
        var type = order[i % order.size]
        val tokens = ph.fr.split(" ").filter { it.isNotBlank() }
        // Fallbacks so every question is answerable
        if (type == QType.ORDER && tokens.size < 2) type = QType.MEANING_TO_FR
        if (type == QType.CLOZE && tokens.size < 2) type = QType.FR_TO_MEANING
        if (type == QType.CONJUGATE && lesson.id != "verbs" && lesson.id != "past") type = QType.TYPE
        if (type == QType.MATCH && lesson.phrases.size < 3) type = QType.FR_TO_MEANING
        if (type == QType.REPLAY && lesson.phrases.size < 2) type = QType.ORDER
        if (type == QType.STORY && storyForUnit(lesson.unitId) == null) type = QType.FR_TO_MEANING
        // U6 capstone (§4.1): "no training wheels" — multiple choice becomes open typing/speaking.
        if (lesson.unitId == "u6") type = when (type) {
            QType.FR_TO_MEANING -> QType.TYPE
            QType.MEANING_TO_FR -> QType.SPEAK
            QType.LISTEN -> QType.DICTATION
            QType.MATCH -> QType.ORDER
            QType.CLOZE -> QType.TYPE
            QType.MINIMAL_PAIR -> QType.DICTATION
            else -> type
        }

        fun meaningOptions(): List<String> {
            val right = ph.meaning(lang)
            val wrong = ALL_PHRASES.filter { it.fr != ph.fr }.map { it.meaning(lang) }
                .filter { it != right }.distinct().shuffled().take(3)
            return (wrong + right).shuffled()
        }
        fun frenchOptions(): List<String> {
            val wrong = ALL_PHRASES.filter { it.fr != ph.fr }.map { it.fr }.distinct().shuffled().take(3)
            return (wrong + ph.fr).shuffled()
        }

        when (type) {
            QType.FR_TO_MEANING, QType.LISTEN -> Question(type, ph, meaningOptions(), ph.meaning(lang))
            QType.MEANING_TO_FR -> Question(type, ph, frenchOptions(), ph.fr)
            QType.ORDER -> Question(type, ph, answer = tokens.joinToString(" "), words = tokens.shuffled())
            QType.SPEAK -> Question(type, ph, answer = ph.fr)
            QType.CLOZE -> {
                val (display, missing) = makeCloze(ph)
                val distract = ALL_PHRASES.flatMap { it.fr.split(" ") }.map { it.trim { c -> !c.isLetterOrDigit() } }
                    .filter { it.length >= 3 && !it.equals(missing, true) }.distinct().shuffled().take(3)
                Question(type, ph, options = (distract + missing).shuffled(), answer = missing, clozeDisplay = display, clozeMissing = missing)
            }
            QType.MATCH -> {
                val pairs = lesson.phrases.shuffled().take(4).map { it.fr to it.meaning(lang) }
                Question(type, ph, matchPairs = pairs, answer = pairs.toString())
            }
            QType.TYPE -> Question(type, ph, answer = ph.fr, hint = ph.meaning(lang))
            QType.DICTATION -> Question(type, ph, answer = ph.fr, hint = ph.meaning(lang))
            QType.MINIMAL_PAIR -> {
                val pair = MINIMAL_PAIRS.random()
                val correctFirst = Math.random() < 0.5
                val correct = if (correctFirst) pair.a else pair.b
                val other = if (correctFirst) pair.b else pair.a
                val correctMean = if (correctFirst) pair.meanA else pair.meanB
                Question(
                    type, Phrase(correct, correctMean, correctMean),
                    options = listOf(pair.a, pair.b).shuffled(), answer = correct,
                    hint = "Listen carefully…"
                )
            }
            QType.REPLAY -> {
                val lines = lesson.phrases.shuffled().take(2).map { it.fr }
                Question(type, ph, answer = lines.joinToString("\n"), words = lines.shuffled(), replayLines = lines)
            }
            QType.STORY -> {
                val story = storyForUnit(lesson.unitId) ?: STORIES.first()
                val q = story.questions.random()
                Question(type, ph, options = q.options, answer = q.answer, storyText = story.sentences.joinToString(" "), storyQuestion = q.text, hint = story.title)
            }
            QType.CONJUGATE -> {
                val v = VERBS.random()
                val pron = listOf("je", "tu", "il", "nous", "vous", "ils").random()
                val ans = "$pron ${v.present[pron]}"
                Question(type, Phrase(ans, v.en, v.sw, null, "Conjugate ${v.infinitive}"), answer = v.present[pron] ?: "", verb = v.infinitive, pronoun = pron, hint = "${v.infinitive} / $pron")
            }
        }
    }
}

// ---- speech-answer matching (forgiving of accents / punctuation) ----
fun norm(s: String): String =
    Normalizer.normalize(s.lowercase(), Normalizer.Form.NFD)
        .replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "")
        .replace(Regex("[^a-z0-9 ]"), " ")
        .replace(Regex("\\s+"), " ")
        .trim()

private fun lev(a: String, b: String): Int {
    val dp = IntArray(b.length + 1) { it }
    for (i in 1..a.length) {
        var prev = dp[0]
        dp[0] = i
        for (j in 1..b.length) {
            val tmp = dp[j]
            dp[j] = minOf(dp[j] + 1, dp[j - 1] + 1, prev + if (a[i - 1] == b[j - 1]) 0 else 1)
            prev = tmp
        }
    }
    return dp[b.length]
}

fun similarity(a: String, b: String): Double {
    val x = norm(a)
    val y = norm(b)
    if (x.isEmpty() || y.isEmpty()) return 0.0
    return 1.0 - lev(x, y).toDouble() / maxOf(x.length, y.length)
}

fun gradeTyped(input: String, answer: String): Boolean {
    if (norm(input) == norm(answer)) return true
    return similarity(input, answer) >= 0.85
}

fun gradeOrder(usedWords: List<String>, answer: String): Boolean {
    return norm(usedWords.joinToString(" ")) == norm(answer)
}

// ---- JSON serialization for custom lessons / word storage ----
fun phraseToJson(p: Phrase): JSONObject = JSONObject()
    .put("fr", p.fr).put("en", p.en).put("sw", p.sw)
    .put("sheng", p.sheng ?: "").put("tip", p.tip ?: "")
    .put("grammar", p.grammar ?: "").put("level", p.level).put("core", p.core)

fun phraseFromJson(p: JSONObject): Phrase = Phrase(
    p.optString("fr"), p.optString("en"), p.optString("sw"),
    p.optString("sheng").ifBlank { null }, p.optString("tip").ifBlank { null },
    p.optString("grammar").ifBlank { null },
    p.optString("level", "A1").ifBlank { "A1" },
    p.optBoolean("core", false)
)

fun lessonToJson(l: Lesson): JSONObject = JSONObject()
    .put("id", l.id).put("emoji", l.emoji).put("fr", l.fr).put("en", l.en).put("sw", l.sw)
    .put("unitId", l.unitId).put("culture", l.culture ?: "")
    .put("phrases", JSONArray(l.phrases.map { phraseToJson(it) }))

fun lessonFromJson(o: JSONObject): Lesson {
    val arr = o.getJSONArray("phrases")
    val phs = (0 until arr.length()).map { i -> phraseFromJson(arr.getJSONObject(i)) }
    return Lesson(o.optString("id"), o.optString("emoji", "📚"), o.optString("fr"), o.optString("en"), o.optString("sw"), phs, o.optString("unitId", "u4"), o.optString("culture").ifBlank { null })
}
