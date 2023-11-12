import java.sql.Array;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
public class Movie {
    public static HashSet<String> genreSet = new HashSet<String>();
    private String id;
    private String title;

    private int year;

    private String director;

    private HashSet<String> genres;

    private static final Map<String, ArrayList<String>> genreMap = Map.ofEntries(
            Map.entry("stage musical", new ArrayList<String>(){{add("Musical");}}),
            Map.entry("susp", new ArrayList<String>(){{add("Thriller");}}),
            Map.entry("bio", new ArrayList<String>(){{add("Biographical Picture");}}),
            Map.entry("advt", new ArrayList<String>(){{add("Adventure");}}),
            Map.entry("cond", new ArrayList<String>(){{add("Comedy");}}),
            Map.entry("cart", new ArrayList<String>(){{add("Cartoon");}}),
            Map.entry("docu", new ArrayList<String>(){{add("Documentary");}}),
            Map.entry("hist", new ArrayList<String>(){{add("History");}}),
            Map.entry("comd west", new ArrayList<String>(){{add("Comedy");add("Western");}}),
            Map.entry("surr", new ArrayList<String>(){{add("Surreal");}}),
            Map.entry("dram", new ArrayList<String>(){{add("Drama");}}),
            Map.entry("road", new ArrayList<String>(){{add("Road");}}),
            Map.entry("noir comd romt", new ArrayList<String>(){{add("Black");add("Comedy");add("Romantic");}}),
            Map.entry("comd noir", new ArrayList<String>(){{add("Comedy");add("Black");}}),
            Map.entry("romt actn", new ArrayList<String>(){{add("Violence");add("Romantic");}}),
            Map.entry("anti-dram", new ArrayList<String>(){{add("Anti-Drama");}}),
            Map.entry("ram", new ArrayList<String>(){{add("Drama");}}),
            Map.entry("ctxx", new ArrayList<String>(){{add("Uncategorized");}}),
            Map.entry("scfi", new ArrayList<String>(){{add("Science Fiction");}}),
            Map.entry("epic", new ArrayList<String>(){{add("Epic");}}),
            Map.entry("surl", new ArrayList<String>(){{add("Surreal");}}),
            Map.entry("kinky", new ArrayList<String>(){{add("Kinky");}}),
            Map.entry("comd", new ArrayList<String>(){{add("Comedy");}}),
            Map.entry("romtx", new ArrayList<String>(){{add("Romantic");}}),
            Map.entry("actn", new ArrayList<String>(){{add("Violence");}}),
            Map.entry("romt dram", new ArrayList<String>(){{add("Drama");add("Romantic");}}),
            Map.entry("verite", new ArrayList<String>(){{add("Verite");}}),
            Map.entry("ctcxx", new ArrayList<String>(){{add("Uncategorized");}}),
            Map.entry("mystp", new ArrayList<String>(){{add("Mystery");}}),
            Map.entry("myst", new ArrayList<String>(){{add("Mystery");}}),
            Map.entry("cnrbb", new ArrayList<String>(){{add("Cops and Robbers");}}),
            Map.entry("muscl", new ArrayList<String>(){{add("Musical");}}),
            Map.entry("biop", new ArrayList<String>(){{add("Biographical Picture");}}),
            Map.entry("natu", new ArrayList<String>(){{add("Nature");}}),
            Map.entry("draam", new ArrayList<String>(){{add("Drama");}}),
            Map.entry("cmr", new ArrayList<String>(){{add("Cops and Robbers");}}),
            Map.entry("hor", new ArrayList<String>(){{add("Horror");}}),
            Map.entry("noir comd", new ArrayList<String>(){{add("Black");add("Comedy");}}),
            Map.entry("porn", new ArrayList<String>(){{add("Pornography");}}),
            Map.entry("dram docu", new ArrayList<String>(){{add("Documentary");add("Drama");}}),
            Map.entry("cult", new ArrayList<String>(){{add("Cult");}}),
            Map.entry("scif", new ArrayList<String>(){{add("Science Fiction");}}),
            Map.entry("rfp; h*", new ArrayList<String>(){{add("Rfp");add("H*");}}),
            Map.entry("porb", new ArrayList<String>(){{add("Pornography");}}),
            Map.entry("romt. comd", new ArrayList<String>(){{add("Comedy");add("Romantic");}}),
            Map.entry("ca", new ArrayList<String>(){{add("Ca");}}),
            Map.entry("surreal", new ArrayList<String>(){{add("Surreal");}}),
            Map.entry("cnr", new ArrayList<String>(){{add("Cops and Robbers");}}),
            Map.entry("h", new ArrayList<String>(){{add("H");}}),
            Map.entry("allegory", new ArrayList<String>(){{add("Allegory");}}),
            Map.entry("romtadvt", new ArrayList<String>(){{add("Adventure");add("Romantic");}}),
            Map.entry("biog", new ArrayList<String>(){{add("Biographical Picture");}}),
            Map.entry("weird", new ArrayList<String>(){{add("Weird");}}),
            Map.entry("comdx", new ArrayList<String>(){{add("Comedy");}}),
            Map.entry("biob", new ArrayList<String>(){{add("Biographical Picture");}}),
            Map.entry("txx", new ArrayList<String>(){{add("Uncategorized");}}),
            Map.entry("sati", new ArrayList<String>(){{add("Satire");}}),
            Map.entry("psyc", new ArrayList<String>(){{add("Psychological");}}),
            Map.entry("tvmini", new ArrayList<String>(){{add("TV Miniseries");}}),
            Map.entry("tv", new ArrayList<String>(){{add("TV Show");}}),
            Map.entry("biopx", new ArrayList<String>(){{add("Biographical Picture");}}),
            Map.entry("dist", new ArrayList<String>(){{add("Disaster");}}),
            Map.entry("romt", new ArrayList<String>(){{add("Romantic");}}),
            Map.entry("duco", new ArrayList<String>(){{add("Documentary");}}),
            Map.entry("act", new ArrayList<String>(){{add("Violence");}}),
            Map.entry("west", new ArrayList<String>(){{add("Western");}}),
            Map.entry("dicu", new ArrayList<String>(){{add("Documentary");}}),
            Map.entry("noir", new ArrayList<String>(){{add("Black");}}),
            Map.entry("biopp", new ArrayList<String>(){{add("Biographical Picture");}}),
            Map.entry("adctx", new ArrayList<String>(){{add("Ctxx");}}),
            Map.entry("dram.actn", new ArrayList<String>(){{add("Drama");add("Romantic");}}),
            Map.entry("h**", new ArrayList<String>(){{add("H**");}}),
            Map.entry("art video", new ArrayList<String>(){{add("Art");}}),
            Map.entry("camp", new ArrayList<String>(){{add("Now-Camp");}}),
            Map.entry("avant garde", new ArrayList<String>(){{add("Avant Garde");}}),
            Map.entry("romt fant", new ArrayList<String>(){{add("Fantasy");add("Romantic");}}),
            Map.entry("faml", new ArrayList<String>(){{add("Family");}}),
            Map.entry("scat", new ArrayList<String>(){{add("Scat");}}),
            Map.entry("horr", new ArrayList<String>(){{add("Horror");}}),
            Map.entry("disa", new ArrayList<String>(){{add("Disaster");}}),
            Map.entry("psych dram", new ArrayList<String>(){{add("Psychological");add("Drama");}}),
            Map.entry("dramn", new ArrayList<String>(){{add("Drama");}}),
            Map.entry("docu dram", new ArrayList<String>(){{add("Documentary");add("Drama");}}),
            Map.entry("h0", new ArrayList<String>(){{add("H0");}}),
            Map.entry("musc", new ArrayList<String>(){{add("Musical");}}),
            Map.entry("ctxxx", new ArrayList<String>(){{add("Ctxx");}}),
            Map.entry("drama", new ArrayList<String>(){{add("Drama");}}),
            Map.entry("undr", new ArrayList<String>(){{add("Undr");}}),
            Map.entry("s.f.", new ArrayList<String>(){{add("Science Fiction");}}),
            Map.entry("homo", new ArrayList<String>(){{add("Homo");}}),
            Map.entry("fanth*", new ArrayList<String>(){{add("Fantasy");add("H*");}}),
            Map.entry("dramd", new ArrayList<String>(){{add("Drama");}}),
            Map.entry("axtn", new ArrayList<String>(){{add("Violence");}}),
            Map.entry("west1", new ArrayList<String>(){{add("Western");}}),
            Map.entry("sxfi", new ArrayList<String>(){{add("Science Fiction");}}),
            Map.entry("expm", new ArrayList<String>(){{add("Expm");}}),
            Map.entry("avga", new ArrayList<String>(){{add("Avant Garde");}}),
            Map.entry("sports", new ArrayList<String>(){{add("Sports");}}),
            Map.entry("viol", new ArrayList<String>(){{add("Violence");}}),
            Map.entry("muusc", new ArrayList<String>(){{add("Musical");}}),
            Map.entry("ducu", new ArrayList<String>(){{add("Documentary");}}),
            Map.entry("fant", new ArrayList<String>(){{add("Fantasy");}}),
            Map.entry("dram>", new ArrayList<String>(){{add("Drama");}}),
            Map.entry("cnrb", new ArrayList<String>(){{add("Cops and Robbbers");}}),
            Map.entry("ront", new ArrayList<String>(){{add("Romantic");}}),
            Map.entry("romt comd", new ArrayList<String>(){{add("Romantic");add("Comedy");}}),
            Map.entry("adct", new ArrayList<String>(){{add("adct");}}),
            Map.entry("crim", new ArrayList<String>(){{add("Criminal");}}),
            Map.entry("sctn", new ArrayList<String>(){{add("Sctn");}})
    );

//    [stage musical, susp, bio, advt, cond, cart, docu, hist, comd west,
//    surr, dram, road, noir comd romt, comd noir, romt actn, anti-dram,
//    ram, ctxx, scfi, epic, surl, kinky, comd, biop , romtx, actn, romt dram,
//    docu , verite, ctcxx, mystp, myst, cnrbb, muscl, biop, natu, draam, cmr,
//    susp , hor, noir comd, porn, dram docu, cult, scif, porn , porb,
//    romt. comd, ca, surreal, cnr, h, allegory, romtadvt, biog, weird,
//    comdx, biob, txx, sati, psyc,     susp, tvmini, tv, romt , biopx,
//    dist, muscl , myst , dram , romt, duco, rfp; h* , act, horr , west,
//    dicu, noir, advt , biopp, cart , adctx, dram.actn, h**, art video,
//    camp, avant garde, fant , romt fant, faml, scat, horr, fanth* , disa,
//    psych dram, dramn, docu dram, h0, musc, ctxxx, drama, undr, s.f., homo,
//    comd , dramd, axtn, west1, sxfi, expm, avga, sports, viol, muusc, ducu,
//    fant, dram>, cnrb, ront, romt comd, adct, crim, sctn]

    public Movie(){
        this.genres = new HashSet<String>();

    }

    public Movie(String id, String title, int year, String director) {
        this.id = id;
        this.title = title;
        this.year  = year;
        this.director = director;
        this.genres = new HashSet<String>();


    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getDirector() {
        return director;
    }
    public void setDirector(String director) {
        this.director = director;
    }

    public int getYear() {
        return year;
    }
    public void setYear(int year) {
        this.year = year;
    }

    public HashSet<String> getGenres() {return genres;};
    public void appendGenre(String genre)
    {
        if (!genre.isEmpty()) {
            for (String g: genreMap.get(genre.toLowerCase().trim())) {
                genres.add(g);
                genreSet.add(g);
            }

        }

    }

    public void printSet() {
        System.out.println(genreSet);
    }
    public void printMap() {
        System.out.println(genreMap);
    }

    public String insertFormat() {
        return "(\"" + id + "\", \"" + title + "\", " + year + ", \"" + director + "\")";
    }



    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Movie Details - ");
        sb.append("Title:" + getTitle());
        sb.append(", ");
        sb.append("Id:" + getId());
        sb.append(", ");
        sb.append("Director:" + getDirector());
        sb.append(", ");
        sb.append("Year:" + getYear());
        sb.append(", ");
        sb.append("Genres:" + genres);
        sb.append(".");

        return sb.toString();
    }
}
