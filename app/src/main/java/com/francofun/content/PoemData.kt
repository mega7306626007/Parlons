package com.francofun.content

import android.content.Context
import java.io.File

/**
 * Real French poems, full text, public domain — fetched from Wikisource
 * (19th-century editions) and bundled for offline reading. Lengths vary
 * naturally from short sonnets to long odes. No links, no redirects.
 */

data class Poem(
    val id: String,
    val title: String,
    val poet: String,
    val topic: String,
    val level: String,
    val year: Int,
    val source: String,
    val frText: String,
    val enText: String
)

val POEMS: List<Poem> = listOf(
    Poem(
        id = "rimbaud-voyelles", title = "Voyelles", poet = "Arthur Rimbaud",
        topic = "Couleurs", level = "B2", year = 1871,
        source = "Poésies complètes, éd. Vanier, 1895 (Wikisource)",
        frText = "A noir, E blanc, I rouge, U vert, O bleu, voyelles,\nJe dirai quelque jour vos naissances latentes.\nA, noir corset velu des mouches éclatantes\nQui bombillent autour des puanteurs cruelles,\nGolfe d'ombre ; E, candeur des vapeurs et des tentes,\nLance des glaciers fiers, rois blancs, frissons d'ombelles ;\nI, pourpres, sang craché, rire des lèvres belles\nDans la colère ou les ivresses pénitentes ;\nU, cycles, vibrements divins des mers virides,\nPaix des pâtis semés d'animaux, paix des rides\nQue l'alchimie imprime aux grands fronts studieux ;\nO, suprême Clairon plein des strideurs étranges,\nSilences traversés des Mondes et des Anges :\n— O l'Oméga, rayon violet de Ses Yeux !",
        enText = "Rimbaud's famous sonnet giving each vowel a colour. Theme: synesthesia — hearing colours, seeing sounds. Key words: la voyelle (vowel), le sang (blood), la paix (peace), suprême (supreme)."
    ),
    Poem(
        id = "rimbaud-ma-boheme", title = "Ma Bohême", poet = "Arthur Rimbaud",
        topic = "Voyage", level = "B1", year = 1870,
        source = "Poésies complètes, éd. Vanier, 1895 (Wikisource)",
        frText = "Je m'en allais, les poings dans mes poches crevées ;\nMon paletot aussi devenait idéal ;\nJ'allais sous le ciel, Muse ! et j'étais ton féal ;\nOh ! là là ! que d'amours splendides j'ai rêvées !\nMon unique culotte avait un large trou.\n— Petit Poucet rêveur, j'égrenais dans ma course\nDes rimes. Mon auberge était à la Grande-Ourse ;\n— Mes étoiles au ciel avaient un doux frou-frou.\nEt je les écoutais, assis au bord des routes,\nCes bons soirs de septembre où je sentais des gouttes\nDe rosée à mon front, comme un vin de vigueur ;\nOù, rimant au milieu des ombres fantastiques,\nComme des lyres, je tirais les élastiques\nDe mes souliers blessés, un pied près de mon cœur !",
        enText = "A teenage runaway walking the roads, pockets torn, dreaming rhymes under the September stars. Theme: freedom and youth. Key words: s'en aller (to set off), rêver (to dream), l'étoile (star), le cœur (heart)."
    ),
    Poem(
        id = "rimbaud-sensation", title = "Sensation", poet = "Arthur Rimbaud",
        topic = "Nature", level = "A2", year = 1870,
        source = "Poésies complètes, éd. Vanier, 1895 (Wikisource)",
        frText = "Par les soirs bleus d'été, j'irai dans les sentiers,\nPicoté par les blés, fouler l'herbe menue :\nRêveur, j'en sentirai la fraîcheur à mes pieds.\nJe laisserai le vent baigner ma tête nue !\nJe ne parlerai pas, je ne penserai rien :\nMais l'amour infini me montera dans l'âme,\nEt j'irai loin, bien loin, comme un bohémien\nPar la Nature, — heureux comme avec une femme.",
        enText = "A summer evening walk through blue wheat fields — pure sensation, no thinking. Theme: nature and bliss. Key words: le soir (evening), l'herbe (grass), le vent (wind), heureux (happy)."
    ),
    Poem(
        id = "verlaine-il-pleure", title = "Il pleure dans mon cœur", poet = "Paul Verlaine",
        topic = "Mélancolie", level = "B1", year = 1874,
        source = "Romances sans paroles, 1902 (Wikisource)",
        frText = "Il pleure dans mon cœur\nComme il pleut sur la ville,\nQuelle est cette langueur\nQui pénètre mon cœur ?\nÔ bruit doux de la pluie\nPar terre et sur les toits !\nPour un cœur qui s'ennuie,\nÔ le chant de la pluie !\nIl pleure sans raison\nDans ce cœur qui s'écœure.\nQuoi ! nulle trahison ?\nCe deuil est sans raison.\nC'est bien la pire peine\nDe ne savoir pourquoi,\nSans amour et sans haine,\nMon cœur a tant de peine !",
        enText = "Rain on the city, tears in the heart — sadness without a reason, Verlaine's most musical poem. Theme: melancholy. Key words: pleurer (to cry), pleuvoir (to rain), le cœur (heart), la peine (sorrow)."
    ),
    Poem(
        id = "verlaine-green", title = "Green", poet = "Paul Verlaine",
        topic = "Amour", level = "B1", year = 1874,
        source = "Romances sans paroles, 1902 (Wikisource)",
        frText = "Voici des fruits, des fleurs, des feuilles et des branches,\nEt puis voici mon cœur, qui ne bat que pour vous.\nNe le déchirez pas avec vos deux mains blanches\nEt qu'à vos yeux si beaux l'humble présent soit doux.\nJ'arrive tout couvert encore de rosée\nQue le vent du matin vient glacer à mon front.\nSouffrez que ma fatigue, à vos pieds reposée,\nRêve des chers instants qui la délasseront.\nSur votre jeune sein laissez rouler ma tête\nToute sonore encore de vos derniers baisers ;\nLaissez-la s'apaiser de la bonne tempête,\nEt que je dorme un peu puisque vous reposez.",
        enText = "A lover arriving at dawn, covered in dew, offering fruit, flowers — and his heart. Theme: tender love. Key words: voici (here is), le cœur (heart), dormir (to sleep), reposer (to rest)."
    ),
    Poem(
        id = "baudelaire-correspondances", title = "Correspondances", poet = "Charles Baudelaire",
        topic = "Nature", level = "B2", year = 1868,
        source = "Les Fleurs du mal, 1868 (Wikisource)",
        frText = "La Nature est un temple où de vivants piliers\nLaissent parfois sortir de confuses paroles ;\nL'homme y passe à travers des forêts de symboles\nQui l'observent avec des regards familiers.\nComme de longs échos qui de loin se confondent\nDans une ténébreuse et profonde unité,\nVaste comme la nuit et comme la clarté,\nLes parfums, les couleurs et les sons se répondent.\nIl est des parfums frais comme des chairs d'enfants,\nDoux comme les hautbois, verts comme les prairies,\n— Et d'autres, corrompus, riches et triomphants,\nAyant l'expansion des choses infinies,\nComme l'ambre, le musc, le benjoin et l'encens,\nQui chantent les transports de l'esprit et des sens.",
        enText = "Nature as a temple where perfumes, colours and sounds answer each other — the founding poem of Symbolism. Theme: hidden unity of senses. Key words: le temple (temple), le symbole (symbol), le parfum (scent), l'esprit (mind)."
    ),
    Poem(
        id = "baudelaire-recueillement", title = "Recueillement", poet = "Charles Baudelaire",
        topic = "Nuit", level = "B2", year = 1868,
        source = "Les Fleurs du mal, 1868 (Wikisource)",
        frText = "Sois sage, ô ma Douleur, et tiens-toi plus tranquille.\nTu réclamais le Soir ; il descend ; le voici :\nUne atmosphère obscure enveloppe la ville,\nAux uns portant la paix, aux autres le souci.\nPendant que des mortels la multitude vile,\nSous le fouet du Plaisir, ce bourreau sans merci,\nVa cueillir des remords dans la fête servile,\nMa Douleur, donne-moi la main ; viens par ici,\nLoin d'eux. Vois se pencher les défuntes Années,\nSur les balcons du ciel, en robes surannées ;\nSurgir du fond des eaux le Regret souriant ;\nLe Soleil moribond s'endormir sous une arche,\nEt, comme un long linceul traînant à l'Orient,\nEntends, ma chère, entends la douce Nuit qui marche.",
        enText = "Evening falls on the city; the poet takes his Sorrow by the hand and listens to the Night walking. Theme: consolation at dusk. Key words: la douleur (sorrow), le soir (evening), la nuit (night), sage (calm)."
    ),
    Poem(
        id = "baudelaire-charogne", title = "Une charogne", poet = "Charles Baudelaire",
        topic = "Mort", level = "B2", year = 1868,
        source = "Les Fleurs du mal, 1868 (Wikisource)",
        frText = "Rappelez-vous l'objet que nous vîmes, mon âme,\nCe beau matin d'été si doux :\nAu détour d'un sentier une charogne infâme\nSur un lit semé de cailloux,\nLes jambes en l'air, comme une femme lubrique,\nBrûlante et suant les poisons,\nOuvrait d'une façon nonchalante et cynique\nSon ventre plein d'exhalaisons.\nLe soleil rayonnait sur cette pourriture,\nComme afin de la cuire à point,\nEt de rendre au centuple à la grande Nature\nTout ce qu'ensemble elle avait joint ;\nEt le ciel regardait la carcasse superbe\nComme une fleur s'épanouir.\nLa puanteur était si forte, que sur l'herbe\nVous crûtes vous évanouir.\nLes mouches bourdonnaient sur ce ventre putride,\nD'où sortaient de noirs bataillons\nDe larves, qui coulaient comme un épais liquide\nLe long de ces vivants haillons.\nTout cela descendait, montait comme une vague,\nOu s'élançait en pétillant ;\nOn eût dit que le corps, enflé d'un souffle vague,\nVivait en se multipliant.\nEt ce monde rendait une étrange musique,\nComme l'eau courante et le vent,\nOu le grain qu'un vanneur d'un mouvement rhythmique\nAgite et tourne dans son van.\nLes formes s'effaçaient et n'étaient plus qu'un rêve,\nUne ébauche lente à venir\nSur la toile oubliée, et que l'artiste achève\nSeulement par le souvenir.\nDerrière les rochers une chienne inquiète\nNous regardait d'un œil fâché,\nÉpiant le moment de reprendre au squelette\nLe morceau qu'elle avait lâché.\n— Et pourtant vous serez semblable à cette ordure,\nÀ cette horrible infection,\nÉtoile de mes yeux, soleil de ma nature,\nVous, mon ange et ma passion !\nOui ! telle vous serez, ô la reine des grâces,\nAprès les derniers sacrements,\nQuand vous irez, sous l'herbe et les floraisons grasses,\nMoisir parmi les ossements.\nAlors, ô ma beauté ! dites à la vermine\nQui vous mangera de baisers,\nQue j'ai gardé la forme et l'essence divine\nDe mes amours décomposés !",
        enText = "A walk, a rotting carcass in the sun — and the shock ending: you, my beauty, will look the same one day. Only art keeps the divine form. Theme: death and art. Key words: la charogne (carcass), le soleil (sun), la beauté (beauty), garder (to keep)."
    ),
    Poem(
        id = "hugo-veni-vidi-vixi", title = "Veni, vidi, vixi", poet = "Victor Hugo",
        topic = "Deuil", level = "B2", year = 1846,
        source = "Les Contemplations (Wikisource)",
        frText = "J'ai bien assez vécu, puisque dans mes douleurs\nJe marche sans trouver de bras qui me secourent,\nPuisque je ris à peine aux enfants qui m'entourent,\nPuisque je ne suis plus réjoui par les fleurs ;\nPuisqu'au printemps, quand Dieu met la nature en fête,\nJ'assiste, esprit sans joie, à ce splendide amour ;\nPuisque je suis à l'heure où l'homme fuit le jour,\nHélas ! et sent de tout la tristesse secrète ;\nPuisque l'espoir serein dans mon âme est vaincu ;\nPuisqu'en cette saison des parfums et des roses,\nÔ ma fille ! j'aspire à l'ombre où tu reposes,\nPuisque mon cœur est mort, j'ai bien assez vécu.\nJe n'ai pas refusé ma tâche sur la terre.\nMon sillon ? Le voilà. Ma gerbe ? La voici.\nJ'ai vécu souriant, toujours plus adouci,\nDebout, mais incliné du côté du mystère.\nJ'ai fait ce que j'ai pu ; j'ai servi, j'ai veillé,\nEt j'ai vu bien souvent qu'on riait de ma peine.\nJe me suis étonné d'être un objet de haine,\nAyant beaucoup souffert et beaucoup travaillé.\nDans ce bagne terrestre où ne s'ouvre aucune aile,\nSans me plaindre, saignant, et tombant sur les mains,\nMorne, épuisé, raillé par les forçats humains,\nJ'ai porté mon chaînon de la chaîne éternelle.\nMaintenant, mon regard ne s'ouvre qu'à demi ;\nJe ne me tourne plus même quand on me nomme ;\nJe suis plein de stupeur et d'ennui, comme un homme\nQui se lève avant l'aube et qui n'a pas dormi.\nJe ne daigne plus même, en ma sombre paresse,\nRépondre à l'envieux dont la bouche me nuit.\nÔ seigneur ! ouvrez-moi les portes de la nuit,\nAfin que je m'en aille et que je disparaisse !",
        enText = "Hugo mourning his drowned daughter Léopoldine: he has lived enough, served and suffered, and now asks only for the doors of night. Theme: grief. Key words: vivre (to live), souffrir (to suffer), la fille (daughter), la nuit (night)."
    ),
    Poem(
        id = "verlaine-automne", title = "Chanson d'automne", poet = "Paul Verlaine",
        topic = "Automne", level = "A1", year = 1866,
        source = "Poèmes saturniens, 1866 (Wikisource)",
        frText = "Les sanglots longs\nDes violons\nDe l'automne\nBlessent mon cœur\nD'une langueur\nMonotone.\nTout suffocant\nEt blême, quand\nSonne l'heure,\nJe me souviens\nDes jours anciens\nEt je pleure ;\nEt je m'en vais\nAu vent mauvais\nQui m'emporte\nDeçà, delà,\nPareil à la\nFeuille morte.",
        enText = "Autumn violins wound the heart; memory brings tears; the poet drifts like a dead leaf. Short lines = perfect dictation and rhythm practice. Theme: melancholy. Key words: l'automne (autumn), pleurer (to cry), le vent (wind), la feuille (leaf)."
    ),
    Poem(
        id = "verlaine-coeur", title = "Il pleure dans mon cœur", poet = "Paul Verlaine",
        topic = "Tristesse", level = "B1", year = 1874,
        source = "Romances sans paroles, 1874 (Wikisource)",
        frText = "Il pleure dans mon cœur\nComme il pleut sur la ville ;\nQuelle est cette langueur\nQui pénètre mon cœur ?\nÔ bruit doux de la pluie\nPar terre et sur les toits !\nPour un cœur qui s'ennuie,\nÔ le chant de la pluie !\nIl pleure sans raison\nDans ce cœur qui s'écœure.\nQuoi ! nulle trahison ?…\nCe deuil est sans raison.\nC'est bien la pire peine\nDe ne savoir pourquoi,\nSans amour et sans haine,\nMon cœur a tant de peine !",
        enText = "Rain on the town, tears with no reason: sadness without cause is the worst pain. Theme: causeless sorrow. Key words: pleurer (to cry), pleuvoir (to rain), le cœur (heart), la peine (pain)."
    ),
    Poem(
        id = "rimbaud-dormeur", title = "Le Dormeur du val", poet = "Arthur Rimbaud",
        topic = "Guerre", level = "B2", year = 1870,
        source = "Poésies (Wikisource)",
        frText = "C'est un trou de verdure où chante une rivière,\nAccrochant follement aux herbes des haillons\nD'argent ; où le soleil, de la montagne fière,\nLuit : c'est un petit val qui mousse de rayons.\nUn soldat jeune, bouche ouverte, tête nue,\nEt la nuque baignant dans le frais cresson bleu,\nDort ; il est étendu dans l'herbe, sous la nue,\nPâle dans son lit vert où la lumière pleut.\nLes pieds dans les glaïeuls, il dort. Souriant comme\nSourirait un enfant malade, il fait un somme :\nNature, berce-le chaudement : il a froid.\nLes parfums ne font pas frissonner sa narine ;\nIl dort dans le soleil, la main sur sa poitrine,\nTranquille. Il a deux trous rouges au côté droit.",
        enText = "A green valley, a sleeping young soldier — smiling, cold — with two red holes in his side. The shock ending reframes everything. Theme: war. Key words: dormir (to sleep), le soldat (soldier), la nature (nature), rouge (red)."
    ),
    Poem(
        id = "rimbaud-boheme", title = "Ma Bohème", poet = "Arthur Rimbaud",
        topic = "Voyage", level = "B1", year = 1870,
        source = "Poésies (Wikisource)",
        frText = "Je m'en allais, les poings dans mes poches crevées ;\nMon paletot aussi devenait idéal ;\nJ'allais sous le ciel, Muse ! et j'étais ton féal ;\nOh ! là ! là ! que d'amours splendides j'ai rêvées !\nMon unique culotte avait un large trou.\n— Petit-Poucet rêveur, j'égrenais dans ma course\nDes rimes. Mon auberge était à la Grande-Ourse.\n— Mes étoiles au ciel avaient un doux frou-frou\nEt je les écoutais, assis au bord des routes,\nCes bons soirs de septembre où je sentais des gouttes\nDe rosée à mon front, comme un vin de vigueur ;\nOù, rimant au milieu des ombres fantastiques,\nComme des lyres, je tirais les élastiques\nDe mes souliers blessés, un pied près de mon cœur !",
        enText = "Sixteen, broke, walking France with holes in his pockets, rhyming under the stars. Joy in poverty. Theme: freedom. Key words: aller (to go), rêver (to dream), l'étoile (star), le ciel (sky)."
    ),
    Poem(
        id = "hugo-demain", title = "Demain, dès l'aube", poet = "Victor Hugo",
        topic = "Deuil", level = "B1", year = 1856,
        source = "Les Contemplations, 1856 (Wikisource)",
        frText = "Demain, dès l'aube, à l'heure où blanchit la campagne,\nJe partirai. Vois-tu, je sais que tu m'attends.\nJ'irai par la forêt, j'irai par la montagne.\nJe ne puis demeurer loin de toi plus longtemps.\nJe marcherai les yeux fixés sur mes pensées,\nSans rien voir au dehors, sans entendre aucun bruit,\nSeul, inconnu, le dos courbé, les mains croisées,\nTriste, et le jour pour moi sera comme la nuit.\nJe ne regarderai ni l'or du soir qui tombe,\nNi les voiles au loin descendant vers Harfleur,\nEt quand j'arriverai, je mettrai sur ta tombe\nUn bouquet de houx vert et de bruyère en fleur.",
        enText = "At dawn he walks to his daughter's grave, eyes on his thoughts, bringing holly and heather. France's most famous mourning poem. Theme: pilgrimage of grief. Key words: l'aube (dawn), partir (to leave), la tombe (grave), seul (alone)."
    ),
    Poem(
        id = "fontaine-cigale", title = "La Cigale et la Fourmi", poet = "Jean de La Fontaine",
        topic = "Fable", level = "A1", year = 1668,
        source = "Fables, Livre I, 1668 (Wikisource)",
        frText = "La Cigale, ayant chanté\nTout l'été,\nSe trouva fort dépourvue\nQuand la bise fut venue :\nPas un seul petit morceau\nDe mouche ou de vermisseau.\nElle alla crier famine\nChez la Fourmi sa voisine,\nLa priant de lui prêter\nQuelque grain pour subsister\nJusqu'à la saison nouvelle.\n« Je vous paierai, lui dit-elle,\nAvant l'Oût, foi d'animal,\nIntérêt et principal. »\nLa Fourmi n'est pas prêteuse :\nC'est là son moindre défaut.\n« Que faisiez-vous au temps chaud ?\nDit-elle à cette emprunteuse.\n— Nuit et jour à tout venant\nJe chantais, ne vous déplaise.\n— Vous chantiez ? j'en suis fort aise.\nEh bien ! dansez maintenant. »",
        enText = "The grasshopper sang all summer; winter comes begging; the ant's answer is immortal. Every French child knows it. Theme: foresight. Key words: chanter (to sing), danser (to dance), l'été (summer), l'hiver (winter)."
    ),
    Poem(
        id = "fontaine-corbeau", title = "Le Corbeau et le Renard", poet = "Jean de La Fontaine",
        topic = "Fable", level = "A1", year = 1668,
        source = "Fables, Livre I, 1668 (Wikisource)",
        frText = "Maître Corbeau, sur un arbre perché,\nTenait en son bec un fromage.\nMaître Renard, par l'odeur alléché,\nLui tint à peu près ce langage :\n« Hé ! bonjour, Monsieur du Corbeau.\nQue vous êtes joli ! que vous me semblez beau !\nSans mentir, si votre ramage\nSe rapporte à votre plumage,\nVous êtes le Phénix des hôtes de ces bois. »\nÀ ces mots le Corbeau ne se sent pas de joie ;\nEt pour montrer sa belle voix,\nIl ouvre un large bec, laisse tomber sa proie.\nLe Renard s'en saisit, et dit : « Mon bon Monsieur,\nApprenez que tout flatteur\nVit aux dépens de celui qui l'écoute :\nCette leçon vaut bien un fromage, sans doute. »\nLe Corbeau, honteux et confus,\nJura, mais un peu tard, qu'on ne l'y prendrait plus.",
        enText = "Flattery opens the beak; the cheese falls; the moral is eternal: flatterers live off listeners. Theme: flattery. Key words: le fromage (cheese), flatter (to flatter), la leçon (lesson), honteux (ashamed)."
    ),
    Poem(
        id = "apollinaire-mirabeau", title = "Le Pont Mirabeau", poet = "Guillaume Apollinaire",
        topic = "Amour", level = "B1", year = 1912,
        source = "Alcools, 1913 (Wikisource)",
        frText = "Sous le pont Mirabeau coule la Seine\nEt nos amours\nFaut-il qu'il m'en souvienne\nLa joie venait toujours après la peine\nVienne la nuit sonne l'heure\nLes jours s'en vont je demeure\nLes mains dans les mains restons face à face\nTandis que sous\nLe pont de nos bras passe\nDes éternels regards l'onde si lasse\nVienne la nuit sonne l'heure\nLes jours s'en vont je demeure\nL'amour s'en va comme cette eau courante\nL'amour s'en va\nComme la vie est lente\nEt comme l'Espérance est violente\nVienne la nuit sonne l'heure\nLes jours s'en vont je demeure\nPassent les jours et passent les semaines\nNi temps passé\nNi les amours reviennent\nSous le pont Mirabeau coule la Seine\nVienne la nuit sonne l'heure\nLes jours s'en vont je demeure",
        enText = "Under the bridge flows the Seine; love flows away like water; days pass, the poet remains. No punctuation — time itself drifts. Theme: passing love. Key words: couler (to flow), l'amour (love), la nuit (night), demeurer (to remain)."
    ),
    Poem(
        id = "baudelaire-albatros", title = "L'Albatros", poet = "Charles Baudelaire",
        topic = "Poète", level = "B2", year = 1861,
        source = "Les Fleurs du mal, 1861 (Wikisource)",
        frText = "Souvent, pour s'amuser, les hommes d'équipage\nPrennent des albatros, vastes oiseaux des mers,\nQui suivent, indolents compagnons de voyage,\nLe navire glissant sur les gouffres amers.\nÀ peine les ont-ils déposés sur les planches,\nQue ces rois de l'azur, maladroits et honteux,\nLaissent piteusement leurs grandes ailes blanches\nComme des avirons traîner à côté d'eux.\nCe voyageur ailé, comme il est gauche et veule !\nLui, naguère si beau, qu'il est comique et laid !\nL'un agace son bec avec un brûle-gueule,\nL'autre mime, en boitant, l'infirme qui volait !\nLe Poète est semblable au prince des nuées\nQui hante la tempête et se rit de l'archer ;\nExilé sur le sol au milieu des huées,\nSes ailes de géant l'empêchent de marcher.",
        enText = "The albatross, king of the skies, is clumsy and mocked on deck — like the Poet among men. The symbol of the misunderstood artist. Theme: the poet. Key words: l'oiseau (bird), l'aile (wing), le poète (poet), exilé (exiled)."
    )
)

fun getPoemById(id: String): Poem? = POEMS.find { it.id == id }

fun getPoemById(context: Context, id: String): Poem? =
    loadUserPoems(context).find { it.id == id } ?: POEMS.find { it.id == id }

fun allPoems(context: Context): List<Poem> = loadUserPoems(context) + POEMS

/* ── User-written poems: persisted in filesDir/user-poems, listed first ── */

private const val USER_POEM_SEP = "\n=====\n"

fun saveUserPoem(
    context: Context,
    title: String,
    topic: String,
    level: String,
    frText: String,
    enText: String
): Poem {
    val dir = File(context.filesDir, "user-poems").apply { mkdirs() }
    val id = "user-poem-${System.currentTimeMillis()}"
    File(dir, "$id.txt").writeText(
        listOf(title, topic, level).joinToString("\n") + "\n" + frText + USER_POEM_SEP + enText
    )
    return Poem(
        id = id, title = title.ifBlank { "Mon poème" }, poet = "Vous",
        topic = topic.ifBlank { "Libre" }, level = level.ifBlank { "A2" },
        year = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR),
        source = "Écrit par vous", frText = frText, enText = enText
    )
}

fun loadUserPoems(context: Context): List<Poem> {
    val dir = File(context.filesDir, "user-poems")
    if (!dir.isDirectory) return emptyList()
    return dir.listFiles { f -> f.isFile && f.name.endsWith(".txt") }
        ?.sortedByDescending { it.lastModified() }
        ?.mapNotNull { f ->
            runCatching {
                val lines = f.readLines()
                if (lines.size < 4) return@runCatching null
                val rest = lines.drop(3).joinToString("\n").split(USER_POEM_SEP, limit = 2)
                Poem(
                    id = f.nameWithoutExtension,
                    title = lines[0].ifBlank { "Mon poème" }, poet = "Vous",
                    topic = lines[1].ifBlank { "Libre" }, level = lines[2].ifBlank { "A2" },
                    year = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR),
                    source = "Écrit par vous",
                    frText = rest.getOrElse(0) { "" }, enText = rest.getOrElse(1) { "" }
                )
            }.getOrNull()
        } ?: emptyList()
}
