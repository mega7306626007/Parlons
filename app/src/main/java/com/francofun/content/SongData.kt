package com.francofun.content

/**
 * 600 French study songs: 150 iconic songs × 4 level editions
 * (A1 Découverte, A2 Karaoké, B1 Analyse, B2 Défi).
 *
 * Copyright-safe by design: each card carries a SHORT hook (1 line),
 * key vocabulary and a study task — never full commercial lyrics.
 * Listen to the official recording for the complete song.
 */

// id|title|artist|genre|year|hookFr|keywords(fr=en;…)|themeFr|themeEn|hookEn
private val SONG_SEEDS = listOf(
    "stromae-formidable|Formidable|Stromae|Electro-pop|2013|Tu étais formidable, j'étais fort minable|formidable=great;minable=pitiful;hier=yesterday;draguer=to chat up|la fierté après la rupture|pride after heartbreak|You were wonderful, I was pitiful",
    "stromae-papaoutai|Papaoutai|Stromae|Electro-pop|2013|Où t'es, papa, où t'es|où=where;chercher=to look for;compter=to count;trouver=to find|l'absence du père|an absent father|Where are you, dad, where are you",
    "stromae-alors-danse|Alors on danse|Stromae|Electro-pop|2010|Alors on danse pour oublier|danser=to dance;oublier=to forget;alors=so;le problème=problem|danser pour oublier|dancing to forget|So we dance to forget",
    "stromae-tous-memes|Tous les mêmes|Stromae|Electro-pop|2013|Vous êtes tous les mêmes|même=same;capricieux=moody;le rendez-vous=date;se disputer=to quarrel|les disputes d'amour|lover's quarrels|You are all the same",
    "stromae-sante|Santé|Stromae|Electro-pop|2021|Santé à ceux qui n'en ont pas|la santé=health;trinquer=to toast;bosser=to work;se lever=to get up|trinquer aux travailleurs|toasting the workers|Cheers to those going without",
    "stromae-lenfer|L'enfer|Stromae|Electro-pop|2022|Je ne suis pas tout seul à être tout seul|seul=alone;l'enfer=hell;réfléchir=to think;la solitude=loneliness|la solitude partagée|shared loneliness|I am not alone in feeling alone",
    "stromae-fils-joie|Fils de joie|Stromae|Electro-pop|2022|On est tous des fils de joie|la joie=joy;danser=to dance;la fête=party;le respect=respect|la fête et le respect|celebration and respect|We are all children of joy",
    "stromae-ave-cesaria|Ave Cesaria|Stromae|Electro-pop|2013|Les divas du Cap-Vert|la diva=diva;chanter=to sing;l'île=island;la morna=morna music|hommage à Cesária Évora|tribute to Cesária Évora|The divas of Cape Verde",
    "indila-derniere|Dernière danse|Indila|Pop|2014|Je veux encore danser|danser=to dance;encore=again;avant que=before;s'arrêter=to stop|une dernière danse|one last dance|I want to dance once more",
    "indila-tourner|Tourner dans le vide|Indila|Pop|2014|Je tourne sans fin|tourner=to spin;le vide=emptiness;sans fin=endless;tomber=to fall|tourner sans fin|spinning endlessly|I spin without end",
    "indila-sos|S.O.S. d'un terrien en détresse|Indila|Pop|2014|S.O.S. d'un cœur qui bat|le cœur=heart;battre=to beat;le monde=world;trembler=to tremble|un appel au secours|a cry for help|S.O.S. from a beating heart",
    "indila-parle-tete|Parle à ta tête|Indila|Pop|2019|Parle à ta tête|parler=to talk;la tête=head;ressentir=to feel;garder=to keep|écouter son cœur|listening to your heart|Talk to your head",
    "indila-love-story|Love Story|Indila|Pop|2014|On s'aime, on se déchire|s'aimer=to love each other;se déchirer=to tear apart;pleurer=to cry;l'histoire=story|une histoire d'amour|a love story|We love, we tear apart",
    "piaf-vie-rose|La Vie en rose|Édith Piaf|Chanson|1947|Je vois la vie en rose|la vie=life;rose=pink;le bonheur=happiness;le cœur=heart|voir la vie en rose|seeing life in pink|I see life in pink",
    "piaf-regrette|Non, je ne regrette rien|Édith Piaf|Chanson|1960|Non, rien de rien|regretter=to regret;rien=nothing;le passé=past;balayer=to sweep away|ne rien regretter|regretting nothing|No, I regret nothing",
    "piaf-hymne|Hymne à l'amour|Édith Piaf|Chanson|1950|Le ciel peut s'effondrer|le ciel=sky;s'effondrer=to collapse;l'amour=love;bleu=blue|l'amour plus fort que tout|love stronger than all|The sky may fall",
    "piaf-milord|Milord|Édith Piaf|Chanson|1959|Allez, Milord, souriez|sourire=to smile;la rue=street;la fille=girl;le milord=gentleman|remonter le moral|cheering someone up|Come on, sir, smile",
    "piaf-padam|Padam, padam|Édith Piaf|Chanson|1951|Cet air qui m'obsède|obséder=to haunt;l'air=tune;le souvenir=memory;le refrain=chorus|une mélodie obsédante|a haunting tune|That tune haunting me",
    "piaf-accordeoniste|L'Accordéoniste|Édith Piaf|Chanson|1940|La fille de joie est triste|l'accordéon=accordion;triste=sad;le bar=bar;chanter=to sing|la chanteuse des rues|the street singer|The street girl is sad",
    "piaf-sous-ciel|Sous le ciel de Paris|Édith Piaf|Chanson|1954|Sous le ciel de Paris marche l'amour|marcher=to walk;l'amour=love;Paris=Paris;le printemps=spring|Paris amoureux|Paris in love|Under Paris skies walks love",
    "aznavour-hier|Hier encore|Charles Aznavour|Chanson|1964|Hier encore, tout était simple|hier=yesterday;encore=still;simple=simple;le temps=time|le temps qui passe|time passing by|Just yesterday, all was simple",
    "aznavour-boheme|La Bohème|Charles Aznavour|Chanson|1966|La bohème, un peu de rêve|la bohème=bohemian life;le rêve=dream;Montmartre=Montmartre;la jeunesse=youth|la jeunesse à Montmartre|youth in Montmartre|Bohemian life, a little dream",
    "aznavour-emmenez|Emmenez-moi|Charles Aznavour|Chanson|1967|Emmenez-moi au bout du monde|emmener=to take;le bout=end;le monde=world;le bateau=boat|partir loin|leaving far away|Take me to the ends of the earth",
    "aznavour-comediens|Les Comédiens|Charles Aznavour|Chanson|1965|Allez, les comédiens|le comédien=actor;la scène=stage;le rideau=curtain;applaudir=to applaud|la vie des artistes|the life of performers|Come on, performers",
    "aznavour-voyais|Je m'voyais déjà|Charles Aznavour|Chanson|1961|Je m'voyais en haut de l'affiche|se voir=to picture oneself;l'affiche=poster;en haut=at the top;rêver=to dream|rêver de gloire|dreaming of fame|I pictured myself headlining",
    "brel-quitte|Ne me quitte pas|Jacques Brel|Chanson|1959|Ne me quitte pas, il faut oublier|quitter=to leave;oublier=to forget;pleurer=to cry;le temps=time|supplier l'amour|begging for love|Don't leave me",
    "brel-amsterdam|Amsterdam|Jacques Brel|Chanson|1964|Dans le port d'Amsterdam|le port=harbour;le marin=sailor;boire=to drink;chanter=to sing|les marins|the sailors|In Amsterdam's harbour",
    "brel-vieux|Les Vieux|Jacques Brel|Chanson|1963|Les vieux ne rêvent plus|vieux=old;rêver=to dream;la pendule=clock;la maison=house|la vieillesse|old age|Old people dream no more",
    "brel-vesoul|Vesoul|Jacques Brel|Chanson|1968|Tu as voulu voir Vesoul|voir=to see;vouloir=to want;la gare=station;le voyage=journey|un voyage absurde|an absurd journey|You wanted to see Vesoul",
    "brel-moribond|Le Moribond|Jacques Brel|Chanson|1961|Adieu, l'ami, je vais mourir|adieu=farewell;mourir=to die;l'ami=friend;le curé=priest|les adieux|farewells|Farewell, friend, I'm dying",
    "brel-amour|Quand on n'a que l'amour|Jacques Brel|Chanson|1956|Quand on n'a que l'amour|avoir=to have;l'amour=love;offrir=to offer;le bonheur=happiness|la force de l'amour|the power of love|When we have only love",
    "brassens-copains|Les Copains d'abord|Georges Brassens|Chanson|1964|Les copains d'abord|le copain=mate;d'abord=first;la bande=gang;naviguer=to sail|l'amitié|friendship|Mates first of all",
    "brassens-auvergnat|Chanson pour l'Auvergnat|Georges Brassens|Chanson|1954|Elle est à toi, cette chanson|la chanson=song;le pain=bread;donner=to give;le pauvre=poor man|la générosité|generosity|This song is for you",
    "brassens-gorille|Le Gorille|Georges Brassens|Chanson|1952|Gare au gorille|gare à=beware;le gorille=gorilla;le juge=judge;la veuve=widow|la satire|satire|Beware the gorilla",
    "brassens-bancs|Les Amoureux des bancs publics|Georges Brassens|Chanson|1953|Ils s'embrassent sur les bancs|s'embrasser=to kiss;le banc=bench;public=public;les amoureux=lovers|les amoureux|young lovers|They kiss on park benches",
    "gainsbourg-javanaise|La Javanaise|Serge Gainsbourg|Chanson|1963|J'avoue, j'en ai bavé|avouer=to admit;baver=to suffer;oublier=to forget;la danse=dance|une danse d'amour|a dance of love|I admit, I suffered",
    "gainsbourg-poinconneur|Le Poinçonneur des Lilas|Serge Gainsbourg|Chanson|1958|Je suis le poinçonneur|le poinçonneur=ticket puncher;le métro=subway;le trou=hole;le ticket=ticket|le métro parisien|the Paris metro|I'm the ticket puncher",
    "gainsbourg-anamour|L'Anamour|Serge Gainsbourg|Chanson|1969|On devrait toujours être amoureux|amoureux=in love;toujours=always;devrait=should;le cœur=heart|rester amoureux|staying in love|We should always be in love",
    "gainsbourg-prevert|La Chanson de Prévert|Serge Gainsbourg|Chanson|1961|Que tu te souviennes|se souvenir=to remember;tant=so much;vouloir=to want;les feuilles=leaves|la mémoire|memory|That you'd remember",
    "gainsbourg-armes|Aux armes et cætera|Serge Gainsbourg|Reggae|1979|Aux armes, citoyens|les armes=weapons;le citoyen=citizen;chanter=to sing;l'hymne=anthem|l'hymne revisité|the anthem revisited|To arms, citizens",
    "barbara-aigle|L'Aigle noir|Barbara|Chanson|1970|Un beau jour, un aigle noir|l'aigle=eagle;noir=black;le ciel=sky;rêver=to dream|le rêve et l'oiseau|dream and bird|One fine day, a black eagle",
    "barbara-nantes|Nantes|Barbara|Chanson|1964|Il pleut sur Nantes|pleuvoir=to rain;Nantes=Nantes;le père=father;revenir=to come back|le retour impossible|the impossible return|It's raining on Nantes",
    "trenet-mer|La Mer|Charles Trenet|Chanson|1946|La mer qu'on voit danser|la mer=sea;danser=to dance;voir=to see;l'été=summer|la mer en été|the sea in summer|The sea dancing along",
    "trenet-douce|Douce France|Charles Trenet|Chanson|1947|Douce France, cher pays|doux=sweet;cher=dear;le pays=country;la France=France|la nostalgie du pays|homesickness|Sweet France, dear homeland",
    "trenet-reste|Que reste-t-il de nos amours|Charles Trenet|Chanson|1942|Que reste-t-il de nos amours|rester=to remain;l'amour=love;nos=our;la photo=photo|les amours passées|past loves|What remains of our love",
    "montand-feuilles|Les Feuilles mortes|Yves Montand|Chanson|1949|Les feuilles mortes se ramassent|la feuille=leaf;mort=dead;ramasser=to gather;l'automne=autumn|l'automne|autumn|Dead leaves are gathered",
    "montand-paris|À Paris|Yves Montand|Chanson|1948|À Paris, quand un amour fleurit|fleurir=to bloom;l'amour=love;le printemps=spring;Paris=Paris|Paris au printemps|Paris in spring|In Paris, when love blooms",
    "becaud-nathalie|Nathalie|Gilbert Bécaud|Chanson|1964|La place Rouge était vide|la place=square;rouge=red;vide=empty;Moscou=Moscow|Moscou en hiver|Moscow in winter|Red Square stood empty",
    "becaud-maintenant|Et maintenant|Gilbert Bécaud|Chanson|1961|Et maintenant, que vais-je faire|maintenant=now;faire=to do;aller=to go;pleurer=to cry|après la rupture|after the break-up|And now, what will I do",
    "dalida-paroles|Paroles, paroles|Dalida|Pop|1973|Paroles, encore des paroles|la parole=word;encore=again;le vent=wind;caresser=to caress|des mots en l'air|empty words|Words, more words",
    "dalida-gigi|Gigi l'amoroso|Dalida|Pop|1974|Gigi l'amoroso est mort|mourir=to die;l'amour=love;le village=village;la fête=party|la légende de Gigi|the legend of Gigi|Gigi the lover is dead",
    "dalida-danser|Laissez-moi danser|Dalida|Disco|1979|Laissez-moi danser|danser=to dance;laisser=to let;lundi=Monday;mardi=Tuesday|danser toute la semaine|dancing all week|Let me dance",
    "cfrancois-habitude|Comme d'habitude|Claude François|Chanson|1967|Comme d'habitude, tu rentreras|d'habitude=usually;rentrer=to come home;se coucher=to go to bed;seul=alone|la routine|routine|As usual, you'll come home",
    "cfrancois-alexandrie|Alexandrie Alexandra|Claude François|Disco|1978|Alexandrie, Alexandra|danser=to dance;le soleil=sun;la mer=sea;la fête=party|la fête en Égypte|partying in Egypt|Alexandria, Alexandra",
    "gall-poupee|Poupée de cire, poupée de son|France Gall|Pop|1965|Je suis une poupée de cire|la poupée=doll;la cire=wax;chanter=to sing;le son=sound|être une poupée|being a doll|I'm a wax doll",
    "gall-ella|Ella, elle l'a|France Gall|Pop|1987|C'est comme une gaité|la gaité=cheerfulness;le talent=talent;la force=strength;avoir=to have|le talent|talent|It's like a cheerfulness",
    "gall-resiste|Résiste|France Gall|Pop|1981|Résiste, prouve que tu existes|résister=to resist;prouver=to prove;exister=to exist;le bonheur=happiness|résister|resisting|Resist, prove you exist",
    "johnny-allumer|Allumer le feu|Johnny Hallyday|Rock|1998|Il faudra leur dire|falloir=must;dire=to tell;allumer=to light;le feu=fire|mettre le feu|setting fire|We'll have to tell them",
    "johnny-que-jaime|Que je t'aime|Johnny Hallyday|Rock|1969|Que je t'aime, que je t'aime|aimer=to love;la nuit=night;le jour=day;fort=strong|aimer passionnément|loving passionately|How I love you",
    "johnny-penitencier|Le Pénitencier|Johnny Hallyday|Rock|1964|Les portes du pénitencier|la porte=door;le pénitencier=prison;s'ouvrir=to open;le soleil=sun|la prison|prison|The prison gates",
    "goldman-envole|Envole-moi|Jean-Jacques Goldman|Pop|1984|Envole-moi, envole-moi|s'envoler=to fly away;loin=far;le ciel=sky;la nuit=night|partir loin|flying far away|Fly me away",
    "goldman-je-donne|Je te donne|Jean-Jacques Goldman|Pop|1985|Je te donne mes dernières chemises|donner=to give;la chemise=shirt;dernier=last;tout=all|tout donner|giving everything|I give you my last shirts",
    "goldman-musique|Quand la musique est bonne|Jean-Jacques Goldman|Pop|1982|Quand la musique est bonne|la musique=music;bon=good;danser=to dance;chanter=to sing|la musique|music|When the music's good",
    "cabrel-mourir|Je l'aime à mourir|Francis Cabrel|Pop|1979|Moi, je l'aime à mourir|aimer=to love;mourir=to die;le ciel=sky;les étoiles=stars|aimer fort|loving deeply|I love her to death",
    "cabrel-corrida|La Corrida|Francis Cabrel|Pop|1994|Depuis le temps que je patiente|patienter=to wait;depuis=since;le temps=time;l'arène=arena|contre la corrida|against bullfighting|For so long I've waited",
    "cabrel-marie|Petite Marie|Francis Cabrel|Pop|1977|Petite Marie, je parle de toi|petit=small;parler=to talk;les oiseaux=birds;le ciel=sky|une chanson d'amour|a love song|Little Marie, I speak of you",
    "souchon-foule|Foule sentimentale|Alain Souchon|Pop|1993|On nous inflige des désirs|le désir=desire;infliger=to inflict;la foule=crowd;sentimental=sentimental|la société de consommation|consumer society|Desires inflicted on us",
    "souchon-bobo|Allô maman bobo|Alain Souchon|Pop|1978|Allô maman, bobo|allô=hello;maman=mum;bobo=boo-boo;pleurer=to cry|les petits bobos|little hurts|Hello mum, boo-boo",
    "renaud-mistral|Mistral gagnant|Renaud|Chanson|1985|À m'asseoir sur un banc|s'asseoir=to sit;le banc=bench;rire=to laugh;le temps=time|les souvenirs d'enfance|childhood memories|Sitting on a bench",
    "renaud-vent|Dès que le vent soufflera|Renaud|Chanson|1983|Ce n'est pas l'homme qui prend la mer|prendre=to take;la mer=sea;l'homme=man;le vent=wind|prendre la mer|taking to the sea|It's not man who takes the sea",
    "renaud-hexagone|Hexagone|Renaud|Chanson|1975|Né sous le signe de l'Hexagone|naître=to be born;le signe=sign;la France=France;le mois=month|être français|being French|Born under France's sign",
    "telephone-monde|Un autre monde|Téléphone|Rock|1984|Je rêvais d'un autre monde|rêver=to dream;un autre=another;le monde=world;partir=to leave|rêver d'ailleurs|dreaming elsewhere|I dreamed of another world",
    "telephone-bombe|La Bombe humaine|Téléphone|Rock|1979|C'est une bombe humaine|la bombe=bomb;humain=human;exploser=to explode;la ville=city|la bombe humaine|the human bomb|It's a human bomb",
    "indochine-aventurier|L'Aventurier|Indochine|New wave|1982|Égaré dans la vallée|égaré=lost;la vallée=valley;l'aventure=adventure;le héros=hero|l'aventure|adventure|Lost in the valley",
    "indochine-lune|J'ai demandé à la lune|Indochine|Pop|2002|J'ai demandé à la lune|demander=to ask;la lune=moon;le soleil=sun;briller=to shine|demander à la lune|asking the moon|I asked the moon",
    "farmer-desenchantee|Désenchantée|Mylène Farmer|Pop|1991|N'aie pas peur du noir|avoir peur=to be afraid;le noir=darkness;le monde=world;gris=grey|sans illusions|disenchanted|Don't fear the dark",
    "farmer-libertine|Libertine|Mylène Farmer|Pop|1986|Je suis libertine|libre=free;le cœur=heart;l'amour=love;la nuit=night|la liberté|freedom|I'm libertine",
    "paradis-joe|Joe le taxi|Vanessa Paradis|Pop|1987|Joe le taxi va vite|vite=fast;la main=hand;loin=far;la nuit=night|le taxi de nuit|the night taxi|Joe the taxi drives fast",
    "zaz-veux|Je veux|Zaz|Pop|2010|Je veux de l'amour|vouloir=to want;l'amour=love;l'argent=money;la joie=joy|l'amour pas l'argent|love not money|I want love",
    "zaz-ira|On ira|Zaz|Pop|2013|On ira écouter Harlem|aller=to go;écouter=to listen;le monde=world;ensemble=together|voir le monde|seeing the world|We'll go hear Harlem",
    "mae-attache|On s'attache|Christophe Maé|Pop|2007|On s'attache, on s'emmène|s'attacher=to bond;la vie=life;ensemble=together;le chemin=path|les liens|bonds|We bond, we carry along",
    "pokora-controle|Elle me contrôle|M. Pokora|Pop|2005|Elle me contrôle sans effort|contrôler=to control;l'effort=effort;le regard=look;danser=to dance|être contrôlé|being controlled|She controls me effortlessly",
    "leroy-casse|Cassé|Nolwenn Leroy|Pop|2002|Cassé, mon cœur est cassé|cassé=broken;le cœur=heart;le regard=look;pleurer=to cry|le cœur brisé|broken heart|Broken, my heart is broken",
    "gregoire-toi-moi|Toi + Moi|Grégoire|Pop|2008|Toi et moi, plus eux|toi=you;moi=me;plus=plus;tous=all|toi et moi|you and me|You plus me",
    "pirate-enfants|Comme des enfants|Cœur de Pirate|Indie pop|2009|On s'aime comme des enfants|s'aimer=to love each other;comme=like;enfant=child;comprendre=to understand|l'amour innocent|innocent love|We love like children",
    "gcm-train|Les Voyages en train|Grand Corps Malade|Slam|2006|Les voyages en train, les regards|le voyage=journey;le train=train;le regard=look;les gens=people|les voyages|journeys|Train journeys, glances",
    "gcm-romeo|Roméo kiffe Juliette|Grand Corps Malade|Slam|2007|Roméo habite à Saint-Denis|habiter=to live;la banlieue=suburbs;aimer=to love;la cité=estate|Roméo des cités|suburban Romeo|Romeo lives in Saint-Denis",
    "solaar-bouge|Bouge de là|MC Solaar|Hip-hop|1991|Bouge de là, tu gênes|bouger=to move;gêner=to bother;là=there;la place=spot|bouge de là|move aside|Move, you're in the way",
    "solaar-western|Nouveau Western|MC Solaar|Hip-hop|1994|Far West du béton|le béton=concrete;le far west=far west;la ville=city;le western=western|le western urbain|urban western|Concrete far west",
    "iam-mia|Je danse le mia|IAM|Hip-hop|1994|Je danse le mia|danser=to dance;Marseille=Marseilles;la fête=party;le soleil=sun|danser à Marseille|dancing in Marseilles|I dance the mia",
    "tryo-hymne|L'Hymne de nos campagnes|Tryo|Reggae|1998|Si t'es né dans une cité|naître=to be born;la cité=estate;la campagne=countryside;l'herbe=grass|l'hymne|the anthem|If you were born in a housing estate",
    "zebda-chemise|Tomber la chemise|Zebda|Pop|1999|Tombez la chemise|tomber=to drop;la chemise=shirt;danser=to dance;la fête=party|faire la fête|partying|Take your shirts off",
    "kyo-danse|Dernière danse|Kyo|Rock|2003|J'attendrai ta dernière danse|attendre=to wait;dernier=last;la danse=dance;la nuit=night|la dernière danse|the last dance|I'll wait for your last dance",
    "bbbrunes-dis|Dis-moi|BB Brunes|Rock|2007|Dis-moi ce que tu veux|dire=to tell;vouloir=to want;la nuit=night;venir=to come|dis-moi|tell me|Tell me what you want",
    "louise-vent|J't'emmène au vent|Louise Attaque|Folk|1997|J't'emmène au vent|emmener=to take;le vent=wind;venir=to come;la mer=sea|partir au vent|off with the wind|I'll take you to the wind",
    "noir-vent|Le Vent nous portera|Noir Désir|Rock|2001|Le vent nous portera|le vent=wind;porter=to carry;nous=us;la famille=family|le vent|the wind|The wind will carry us",
    "bashung-nuit|La Nuit je mens|Alain Bashung|Rock|1998|La nuit, je mens|la nuit=night;mentir=to lie;le jour=day;le secret=secret|mentir la nuit|lying at night|At night, I lie",
    "clara-grenade|La Grenade|Clara Luciani|Pop|2018|Sous mon sein, la grenade|le sein=breast;la grenade=grenade;sous=under;cacher=to hide|la grenade|the grenade|Under my breast, the grenade",
    "pomme-brule|On brûlera|Pomme|Pop|2020|On brûlera nos souvenirs|brûler=to burn;le souvenir=memory;le feu=fire;ensemble=together|brûler les souvenirs|burning memories|We'll burn our memories",
    "hoshi-mariniere|Ta marinière|Hoshi|Pop|2018|Ta marinière me va si bien|aller bien=to suit;la marinière=striped top;porter=to wear;si bien=so well|la marinière|the striped top|Your striped top suits me",
    "slimane-fleur|À fleur de toi|Slimane|Pop|2016|À fleur de toi, je vis|vivre=to live;le cœur=heart;aimer=to love;près=near|près de toi|close to you|Close to you, I live",
    "amel-philo|Ma philosophie|Amel Bent|R&B|2004|Je n'ai qu'une philosophie|la philosophie=philosophy;relever=to lift up;la tête=head;le courage=courage|ma philosophie|my philosophy|I have one philosophy",
    "jenifer-soleil|Au soleil|Jenifer|Pop|2002|Au soleil, tout est plus beau|le soleil=sun;beau=beautiful;tout=all;oublier=to forget|au soleil|in the sun|In the sun, all is beautiful",
    "tal-sens|Le Sens de la vie|Tal|Pop|2012|Donne un sens à ta vie|le sens=meaning;la vie=life;donner=to give;chercher=to look for|le sens de la vie|the meaning of life|Give meaning to your life",
    "soulman-soul|Soulman|Ben l'Oncle Soul|Soul|2010|Je suis un soulman|être=to be;chanter=to sing;le cœur=heart;la soul=soul music|le soulman|the soulman|I'm a soulman",
    "cocoon-way|On My Way|Cocoon|Folk|2009|Je marche sur ma route|marcher=to walk;la route=road;la maison=home;aller=to go|en chemin|on the way|I walk along my road",
    "aaron-lili|U-Turn (Lili)|AaRON|Pop|2006|On roulait sur la route|rouler=to drive;la route=road;le voyage=journey;partir=to leave|sur la route|on the road|We drove along the road",
    "lilly-prayer|Prayer in C|Lilly Wood and the Prick|Electro|2014|Quand j'étais enfant|enfant=child;prier=to pray;le ciel=sky;grandir=to grow|la prière|the prayer|When I was a child",
    "bda-bruxelles|Bruxelles|Boulevard des Airs|Pop|2015|Bruxelles, je reviens|revenir=to come back;Bruxelles=Brussels;la ville=city;l'amour=love|revenir à Bruxelles|back to Brussels|Brussels, I'm coming back",
    "tcg-souvenirs|À nos souvenirs|Trois Cafés Gourmands|Folk|2018|À nos souvenirs, on lève nos verres|le souvenir=memory;lever=to raise;le verre=glass;rire=to laugh|nos souvenirs|our memories|To our memories",
    "sers-pourvu|Pourvu|Gauvain Sers|Chanson|2017|Pourvu qu'on s'aime encore|pourvu que=provided that;s'aimer=to love;encore=still;demain=tomorrow|pourvu qu'on s'aime|as long as we love|As long as we still love",
    "capeo-debout|Un homme debout|Claudio Capéo|Pop|2016|Rien ne me fera plier|plier=to bend;debout=standing;l'homme=man;fort=strong|rester debout|staying upright|Nothing will bend me",
    "kendji-gitano|Color Gitano|Kendji Girac|Pop|2014|Color Gitano, ma vie|la couleur=colour;la vie=life;chanter=to sing;gitano=gypsy|couleur gitane|gypsy colour|Gypsy colour, my life",
    "soprano-cosmo|Cosmo|Soprano|Pop|2014|On est les mêmes|même=same;le monde=world;ensemble=together;rêver=to dream|on est les mêmes|we're the same|We're all the same",
    "blackm-route|Sur ma route|Black M|Hip-hop|2014|Sur ma route, j'ai croisé|la route=road;croiser=to cross;le destin=destiny;marcher=to walk|sur ma route|on my road|On my road, I met",
    "sexion-desole|Désolé|Sexion d'Assaut|Hip-hop|2010|Désolé pour hier soir|désolé=sorry;hier soir=last night;pardonner=to forgive;la faute=fault|désolé|sorry|Sorry for last night",
    "youssoupha-connait|On se connaît|Youssoupha|Hip-hop|2012|On se connaît par cœur|se connaître=to know each other;par cœur=by heart;le quartier=neighbourhood;vrai=true|on se connaît|we know each other|We know each other by heart",
    "disiz-plombs|J'pète les plombs|Disiz|Hip-hop|2000|Je pète les plombs|le plomb=fuse;la tête=head;le stress=stress;calmer=to calm|péter les plombs|blowing a fuse|I'm blowing a fuse",
    "kamini-marly|Marly-Gomont|Kamini|Hip-hop|2006|Marly-Gomont, mon village|le village=village;la fierté=pride;la famille=family;le rap=rap|mon village|my village|Marly-Gomont, my village",
    "lartiste-chocolat|Chocolat|Lartiste|Afro-pop|2016|Chocolat, mon chocolat|le chocolat=chocolate;doux=sweet;danser=to dance;la nuit=night|mon chocolat|my chocolate|Chocolate, my chocolate",
    "benabar-diner|Le Dîner|Bénabar|Chanson|2005|Il est où, le bonheur|le bonheur=happiness;où=where;le dîner=dinner;chercher=to look for|où est le bonheur|where is happiness|Where is happiness",
    "fabian-taime|Je t'aime|Lara Fabian|Pop|1996|Je t'aime comme le ciel|aimer=to love;le ciel=sky;la terre=earth;toujours=always|je t'aime|I love you|I love you like the sky",
    "dion-maimes|Pour que tu m'aimes encore|Céline Dion|Pop|1995|Pour que tu m'aimes encore|aimer=to love;encore=again;pour que=so that;le cœur=heart|pour que tu m'aimes|so you'll love me|So that you'll love me still",
    "stpier-trouveras|Tu trouveras|Natasha St-Pier|Pop|2002|Tu trouveras dans ma voix|trouver=to find;la voix=voice;le chemin=path;suivre=to follow|tu trouveras|you will find|You'll find in my voice",
    "badi-entre|Entre nous|Chimène Badi|Pop|2003|Entre nous, pas de mensonges|entre=between;nous=us;le mensonge=lie;vrai=true|entre nous|between us|Between us, no lies",
    "bruel-regarde|Alors regarde|Patrick Bruel|Pop|1989|Alors regarde, regarde|regarder=to look;alors=so;les yeux=eyes;voir=to see|regarde|look|So look, look",
    "sardou-connemara|Les Lacs du Connemara|Michel Sardou|Pop|1981|Terre brûlée au vent|la terre=land;brûlé=burnt;le vent=wind;le lac=lake|le Connemara|Connemara|Wind-burnt land",
    "sardou-amour|La Maladie d'amour|Michel Sardou|Pop|1973|La maladie d'amour court|la maladie=illness;l'amour=love;courir=to run;le cœur=heart|la maladie d'amour|the love bug|The love bug is spreading",
    "mitchell-menthe|Couleur menthe à l'eau|Eddy Mitchell|Rock|1980|Couleur menthe à l'eau|couleur=colour;menthe=mint;l'eau=water;les yeux=eyes|couleur menthe|mint colour|Mint-water colour",
    "lama-malade|Je suis malade|Serge Lama|Chanson|1973|Je suis malade d'amour|malade=ill;l'amour=love;perdu=lost;le cœur=heart|je suis malade|I'm lovesick|I'm sick with love",
    "clerc-preference|Ma préférence|Julien Clerc|Pop|1978|Ma préférence à moi|la préférence=preference;à moi=mine;aimer=to love;le cœur=heart|ma préférence|my preference|My preference, mine",
    "forestier-francisco|San Francisco|Maxime Le Forestier|Folk|1972|C'est une maison bleue|la maison=house;bleu=blue;rêver=to dream;partir=to leave|la maison bleue|the blue house|It's a blue house",
    "voulzy-rocko|Rockollection|Laurent Voulzy|Pop|1977|On a tous dans le cœur|le cœur=heart;tous=all;la chanson=song;les souvenirs=memories|les chansons|the songs|We all hold songs in our hearts",
    "chamfort-manureva|Manureva|Alain Chamfort|Pop|1979|Manureva, le bateau|le bateau=boat;la mer=sea;partir=to leave;loin=far|le bateau|the boat|Manureva, the boat",
    "daho-weekend|Week-end à Rome|Étienne Daho|Pop|1984|Week-end à Rome en amoureux|le week-end=weekend;Rome=Rome;amoureux=in love;partir=to leave|week-end à Rome|weekend in Rome|Weekend in Rome, in love",
    "pagny-savoir|Savoir aimer|Florent Pagny|Pop|1997|Savoir aimer sans rien attendre|savoir=to know;aimer=to love;attendre=to expect;donner=to give|savoir aimer|knowing how to love|Knowing how to love",
    "obispo-lucie|Lucie|Pascal Obispo|Pop|1996|Lucie, j'ai peur|avoir peur=to be afraid;le temps=time;passer=to pass;la nuit=night|Lucie|Lucie|Lucie, I'm afraid",
    "calogero-apesanteur|En apesanteur|Calogero|Pop|2002|En apesanteur, je flotte|flotter=to float;en apesanteur=weightless;le ciel=sky;léger=light|en apesanteur|weightless|Weightless, I float",
    "vianney-vais|Je m'en vais|Vianney|Pop|2016|Je m'en vais, ne t'inquiète pas|s'en aller=to leave;s'inquiéter=to worry;rester=to stay;demain=tomorrow|je m'en vais|I'm leaving|I'm leaving, don't worry",
    "louane-avenir|Avenir|Louane|Pop|2014|J'ai de l'espoir|l'espoir=hope;avoir=to have;l'avenir=future;croire=to believe|l'avenir|the future|I have hope",
    "gims-bella|Bella|Gims|Pop|2013|Bella, tu es partie|partir=to leave;bella=beautiful;la nuit=night;revenir=to come back|Bella|Bella|Bella, you've gone",
    "dadju-reine|Reine|Dadju|R&B|2017|Tu es ma reine|la reine=queen;être=to be;le cœur=heart;ma=my|ma reine|my queen|You are my queen",
    "aya-djadja|Djadja|Aya Nakamura|Afro-pop|2018|Oh Djadja, pas moyen|pas moyen=no way;partir=to leave;jouer=to play;oh=oh|pas moyen|no way|Oh Djadja, no way",
    "angele-balance|Balance ton quoi|Angèle|Pop|2018|Balance ton quoi, balance|balancer=to speak up;quoi=what;parler=to speak;fort=loud|balance ton quoi|speak up|Speak up",
    "bigflo-dommage|Dommage|Bigflo et Oli|Hip-hop|2017|Dommage, on se croisait|dommage=too bad;se croiser=to pass by;le regard=look;chaque jour=every day|dommage|too bad|Too bad, we'd pass each other",
    "orelsan-basique|Basique|Orelsan|Hip-hop|2017|Simple, basique|simple=simple;basique=basic;la vie=life;les choses=things|simple et basique|simple and basic|Simple, basic"
)

private data class Seed(
    val id: String, val title: String, val artist: String, val genre: String, val year: Int,
    val hookFr: String, val keywords: List<Pair<String, String>>, val themeFr: String, val themeEn: String, val hookEn: String
)

private fun parseSeed(row: String): Seed? {
    val p = row.split("|")
    if (p.size != 10) return null
    val year = p[4].toIntOrNull() ?: return null
    val kws = p[6].split(";").mapNotNull { kv ->
        val i = kv.indexOf("=")
        if (i <= 0) null else kv.substring(0, i) to kv.substring(i + 1)
    }
    if (kws.size < 2) return null
    if (p[0].isBlank() || p[1].isBlank() || p[2].isBlank() || p[5].isBlank()) return null
    return Seed(p[0], p[1], p[2], p[3], year, p[5], kws, p[7], p[8], p[9])
}

private fun kwLine(kws: List<Pair<String, String>>): String =
    kws.joinToString(", ") { (fr, en) -> "$fr ($en)" }

/**
 * 150 seeds × 4 editions = 600 study songs.
 * A1 Découverte: hook + first words. A2 Karaoké: hook + sing tip.
 * B1 Analyse: hook + theme + grammar cue. B2 Défi: hook + debate task.
 */
private fun buildSongList(): List<Song> {
    val out = mutableListOf<Song>()
    SONG_SEEDS.mapNotNull(::parseSeed).forEach { s ->
        val first2 = s.keywords.take(2)
        val last2 = s.keywords.takeLast(2)
        out.add(
            Song(
                id = "${s.id}-a1", title = "${s.title} — Découverte", artist = s.artist,
                genre = s.genre, year = s.year,
                frLyrics = "Extrait : « ${s.hookFr}… »\n\n[Mots faciles : ${kwLine(first2)}]\n\nÉcoutez l'original et fredonnez le refrain.",
                enTranslation = "Study hook: “${s.hookEn}…”\n\n[Easy words: ${kwLine(first2)}]\n\nListen to the official recording and hum the chorus."
            )
        )
        out.add(
            Song(
                id = "${s.id}-a2", title = "${s.title} — Karaoké", artist = s.artist,
                genre = s.genre, year = s.year,
                frLyrics = "Extrait : « ${s.hookFr}… »\n\n[Karaoké : chante lentement, appuie chaque syllabe. Mots : ${kwLine(last2)}]\n\nÉcoutez l'original pour le refrain complet.",
                enTranslation = "Study hook: “${s.hookEn}…”\n\n[Karaoke: sing slowly, stress each syllable. Words: ${kwLine(last2)}]\n\nListen to the official recording for the full chorus."
            )
        )
        out.add(
            Song(
                id = "${s.id}-b1", title = "${s.title} — Analyse", artist = s.artist,
                genre = s.genre, year = s.year,
                frLyrics = "Extrait : « ${s.hookFr}… »\n\n[Thème : ${s.themeFr}. Vocabulaire : ${kwLine(s.keywords)}]\n\nQuestion : que raconte cette chanson selon toi ?\n\nÉcoutez l'original pour le refrain complet.",
                enTranslation = "Study hook: “${s.hookEn}…”\n\n[Theme: ${s.themeEn}. Vocabulary: ${kwLine(s.keywords)}]\n\nQuestion: what is this song about, in your view?\n\nListen to the official recording for the full chorus."
            )
        )
        out.add(
            Song(
                id = "${s.id}-b2", title = "${s.title} — Défi", artist = s.artist,
                genre = s.genre, year = s.year,
                frLyrics = "Extrait : « ${s.hookFr}… »\n\n[Défi : résume le thème (${s.themeFr}) en 3 phrases, puis donne ton avis avec « à mon avis » et « parce que ».]",
                enTranslation = "Study hook: “${s.hookEn}…”\n\n[Challenge: summarise the theme (${s.themeEn}) in 3 sentences, then give your opinion.]"
            )
        )
    }
    return out
}

val FRENCH_SONGS: List<Song> by lazy { buildSongList() }

fun getSongById(id: String): Song? = FRENCH_SONGS.find { it.id == id }
