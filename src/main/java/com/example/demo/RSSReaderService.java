package com.example.demo;

import com.apptasticsoftware.rssreader.Item;
import com.apptasticsoftware.rssreader.RssReader;
import com.apptasticsoftware.rssreader.util.ItemComparator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.sound.sampled.*;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class RSSReaderService {

    private static final Set<Item> ITEM_SET = new HashSet<>();
    private static final String AUDIO_FILE = "audio/ring.wav";
    private static final long MAX_HOURS = 12L;
    private boolean running = false;

    private static int RUN_COUNT = 1;
    private static final CharSequence[] KEYWORDS = {
            "fmcg",
            "britannia",
            "marico",
            "titan",
            "srf",
            "hindalco",
            "infosys",
            "upl"
    };

    //Get the links from https://economictimes.indiatimes.com/rss.cms
    // https://www.news18.com/rss/
//    https://www.goodreturns.in/rss/
    // https://www.zeebiz.com/rss
    //https://www.moneycontrol.com/india/newsarticle/rssfeeds/rssfeeds.php
    //https://www.businesstoday.in/rssfeeds/?id=home
    // Copied links till MF
    private static final List<String> URL_List = List.of(
            "https://www.businesstoday.in/rssfeeds/?id=home",
            "https://www.moneycontrol.com/rss/marketreports.xml",
            "https://economictimes.indiatimes.com/news/latest-news/rssfeeds/20989204.cms",
            "https://timesofindia.indiatimes.com/rssfeedstopstories.cms",
            "https://www.livemint.com/rss/markets",
            "https://www.goodreturns.in/rss/business-news-fb.xml",
            "https://www.zeebiz.com/india-markets.xml",
            "https://www.news18.com/rss/markets.xml",
            "https://www.moneycontrol.com/rss/economy.xml",
            "https://economictimes.indiatimes.com/rssfeedsdefault.cms",
            "https://timesofindia.indiatimes.com/rssfeedmostrecent.cms",
            "https://www.livemint.com/rss/companies",
            "https://www.goodreturns.in/rss/partner-content-fb.xml",
            "https://www.news18.com/rss/business.xml",
            "https://www.zeebiz.com/world-economy.xml",
            "https://www.moneycontrol.com/rss/buzzingstocks.xml",
            "https://economictimes.indiatimes.com/rssfeedstopstories.cms",
            "https://timesofindia.indiatimes.com/rssfeeds/-2128936835.cms",
            "https://www.livemint.com/rss/opinion",
            "https://www.goodreturns.in/rss/news-fb.xml",
            "https://www.moneycontrol.com/rss/brokeragerecos.xml",
            "https://economictimes.indiatimes.com/prime/rssfeeds/69891145.cms",
            "https://timesofindia.indiatimes.com/rssfeeds/1898055.cms",
            "https://www.livemint.com/rss/money",
            "https://www.goodreturns.in/rss/money-partner-content-fb.xml",
            "https://www.moneycontrol.com/rss/business.xml",
            "https://economictimes.indiatimes.com/markets/rssfeeds/1977021501.cms",
            "https://www.livemint.com/rss/industry",
            "https://www.goodreturns.in/rss/goodreturns-fb.xml",
            "https://www.moneycontrol.com/rss/mostpopular.xml",
            "https://economictimes.indiatimes.com/news/rssfeeds/1715249553.cms",
            "https://www.livemint.com/rss/news",
            "https://www.goodreturns.in/rss/classroom-fb.xml",
            "https://www.moneycontrol.com/rss/latestnews.xml",
            "https://economictimes.indiatimes.com/industry/rssfeeds/13352306.cms",
            "https://www.livemint.com/rss/Mutual%20Funds",
            "https://www.goodreturns.in/rss/buy-insurance-online-fb.xml",
            "https://economictimes.indiatimes.com/small-biz/rssfeeds/5575607.cms",
            "https://www.goodreturns.in/rss/personal-finance-fb.xml",
            "https://www.goodreturns.in/rss/shares-to-buy-fb.xml",
            "https://www.goodreturns.in/rss/tag-budget.xml",
            "https://tradebrains.in/feed/"

//            "https://economictimes.indiatimes.com/wealth/rssfeeds/837555174.cms",
//            "https://economictimes.indiatimes.com/mf/rssfeeds/359241701.cms",
//            "https://economictimes.indiatimes.com/jobs/rssfeeds/107115.cms",
//            "https://economictimes.indiatimes.com/opinion/rssfeeds/897228639.cms",
//            "https://economictimes.indiatimes.com/nri/rssfeeds/7771250.cms",
//            "https://economictimes.indiatimes.com/magazines/rssfeeds/1466318837.cms",
//            "https://economictimes.indiatimes.com/podcasts/rssfeeds/70700695.cms",
//            "https://economictimes.indiatimes.com/markets/stocks/rssfeeds/2146842.cms",
//            "https://economictimes.indiatimes.com/markets/ipos/fpos/rssfeeds/14655708.cms",
//            "https://economictimes.indiatimes.com/markets/web-stories/rssfeeds/92205028.cms",
//            "https://economictimes.indiatimes.com/markets/mind-over-money/rssfeeds/91256725.cms",
//            "https://economictimes.indiatimes.com/markets/cryptocurrency/rssfeeds/82519373.cms",
//            "https://economictimes.indiatimes.com/markets/commodities/rssfeeds/1808152121.cms",
//            "https://economictimes.indiatimes.com/markets/forex/rssfeeds/1150221130.cms",
//            "https://economictimes.indiatimes.com/markets/live-stream/rssfeeds/93033407.cms",
//            "https://economictimes.indiatimes.com/markets/expert-view/rssfeeds/50649960.cms",
//            "https://economictimes.indiatimes.com/markets/market-moguls/rssfeeds/54953131.cms",
//            "https://economictimes.indiatimes.com/markets/bonds/rssfeeds/2146846.cms",
//            "https://economictimes.indiatimes.com/rsssymbolfeeds/commodityname-Gold.cms",
//            "https://economictimes.indiatimes.com/markets/stocks/rssfeeds/53613060.cms",
//            "https://economictimes.indiatimes.com/prime/technology-and-startups/rssfeeds/63319172.cms",
//            "https://economictimes.indiatimes.com/prime/consumer/rssfeeds/60187420.cms",
//            "https://economictimes.indiatimes.com/prime/money-and-markets/rssfeeds/62511286.cms",
//            "https://economictimes.indiatimes.com/prime/corporate-governance/rssfeeds/63329541.cms",
//            "https://economictimes.indiatimes.com/prime/media-and-communications/rssfeeds/60187277.cms",
//            "https://economictimes.indiatimes.com/prime/transportation/rssfeeds/60187459.cms",
//            "https://economictimes.indiatimes.com/prime/pharma-and-healthcare/rssfeeds/60187434.cms",
//            "https://economictimes.indiatimes.com/prime/fintech-and-bfsi/rssfeeds/60187373.cms",
//            "https://economictimes.indiatimes.com/prime/economy-and-policy/rssfeeds/63884214.cms",
//            "https://economictimes.indiatimes.com/prime/infrastructure/rssfeeds/64403500.cms",
//            "https://economictimes.indiatimes.com/prime/environment/rssfeeds/63319186.cms",
//            "https://economictimes.indiatimes.com/prime/energy/rssfeeds/60187444.cms",
//            "https://economictimes.indiatimes.com/prime/primeshots/rssfeeds/93076384.cms",
//            "https://economictimes.indiatimes.com/prime/prime-vantage/rssfeeds/92539403.cms",
//            "https://economictimes.indiatimes.com/prime/prime-decoder/rssfeeds/93459142.cms",
//            "https://economictimes.indiatimes.com/prime/collections/rssfeeds/93660148.cms",
//            "https://economictimes.indiatimes.com/mf/mf-news/rssfeeds/1107225967.cms",
//            "https://economictimes.indiatimes.com/mf/analysis/rssfeeds/314856258.cms",
//            "https://economictimes.indiatimes.com/mf/elss/rssfeeds/64517243.cms",
//            "https://economictimes.indiatimes.com/mf/learn/rssfeeds/54696002.cms",
//            "https://economictimes.indiatimes.com/wealth/web-stories/rssfeeds/90956882.cms",
//            "https://economictimes.indiatimes.com/wealth/tax/rssfeeds/47119912.cms",
//            "https://economictimes.indiatimes.com/wealth/save/rssfeeds/47119915.cms",
//            "https://economictimes.indiatimes.com/wealth/invest/rssfeeds/48997553.cms",
//            "https://economictimes.indiatimes.com/wealth/insure/rssfeeds/47119917.cms",
//            "https://economictimes.indiatimes.com/wealth/borrow/rssfeeds/48997485.cms",
//            "https://economictimes.indiatimes.com/wealth/earn/rssfeeds/48997527.cms",
//            "https://economictimes.indiatimes.com/wealth/legal/will/rssfeeds/83231363.cms",
//            "https://economictimes.indiatimes.com/wealth/plan/rssfeeds/49674351.cms",
//            "https://economictimes.indiatimes.com/wealth/real-estate/rssfeeds/48997582.cms",
//            "https://economictimes.indiatimes.com/wealth/personal-finance-news/rssfeeds/49674901.cms",
//            "https://economictimes.indiatimes.com/wealth/mutual-funds/rssfeeds/49995327.cms",
//            "https://economictimes.indiatimes.com/wealth/spend/rssfeeds/48997538.cms",
//            "https://economictimes.indiatimes.com/wealth/p2p/rssfeeds/65068593.cms",
//            "https://economictimes.indiatimes.com/wealth/et-wealth/rssfeeds/50943048.cms",
//            "https://economictimes.indiatimes.com/small-biz/sme-sector/rssfeeds/11993058.cms",
//            "https://economictimes.indiatimes.com/small-biz/policy-trends/rssfeeds/11993039.cms",
//            "https://economictimes.indiatimes.com/small-biz/trade/rssfeeds/68806566.cms",
//            "https://economictimes.indiatimes.com/small-biz/entrepreneurship/rssfeeds/11993034.cms",
//            "https://economictimes.indiatimes.com/small-biz/money/rssfeeds/47280660.cms",
//            "https://economictimes.indiatimes.com/small-biz/security-tech/rssfeeds/47280820.cms",
//            "https://economictimes.indiatimes.com/small-biz/legal/rssfeeds/47280656.cms",
//            "https://economictimes.indiatimes.com/small-biz/gst/rssfeeds/58475404.cms",
//            "https://economictimes.indiatimes.com/small-biz/marketing-branding/rssfeeds/47280443.cms",
//            "https://economictimes.indiatimes.com/small-biz/hr-leadership/rssfeeds/47280670.cms",
//            "https://economictimes.indiatimes.com/small-biz/resources/rssfeeds/59663072.cms",
//            "https://economictimes.indiatimes.com/industry/auto/rssfeeds/13359412.cms",
//            "https://economictimes.indiatimes.com/industry/banking/finance/rssfeeds/13358259.cms",
//            "https://economictimes.indiatimes.com/industry/cons-products/rssfeeds/13358759.cms",
//            "https://economictimes.indiatimes.com/industry/energy/rssfeeds/13358350.cms",
//            "https://economictimes.indiatimes.com/industry/renewables/rssfeeds/81585238.cms",
//            "https://economictimes.indiatimes.com/industry/indl-goods/svs/rssfeeds/13357688.cms",
//            "https://economictimes.indiatimes.com/industry/healthcare/biotech/rssfeeds/13358050.cms",
//            "https://economictimes.indiatimes.com/industry/services/rssfeeds/13354120.cms",
//            "https://economictimes.indiatimes.com/industry/media/entertainment/rssfeeds/13357212.cms",
//            "https://economictimes.indiatimes.com/industry/transportation/rssfeeds/13353990.cms",
//            "https://economictimes.indiatimes.com/tech/rssfeeds/13357270.cms",
//            "https://economictimes.indiatimes.com/industry/telecom/rssfeeds/13354103.cms",
//            "https://economictimes.indiatimes.com/industry/miscellaneous/rssfeeds/58456958.cms",
//            "https://economictimes.indiatimes.com/industry/csr/rssfeeds/58571497.cms",
//            "https://economictimes.indiatimes.com/news/india/rssfeeds/81582957.cms",
//            "https://economictimes.indiatimes.com/news/how-to/rssfeeds/85175424.cms",
//            "https://economictimes.indiatimes.com/news/web-stories/rssfeeds/84644705.cms",
//            "https://economictimes.indiatimes.com/news/morning-brief-podcast/rssfeeds/79263773.cms",
//            "https://economictimes.indiatimes.com/news/newsblogs/rssfeeds/65098458.cms",
//            "https://economictimes.indiatimes.com/news/economy/rssfeeds/1373380680.cms",
//            "https://economictimes.indiatimes.com/news/politics-and-nation/rssfeeds/1052732854.cms",
//            "https://economictimes.indiatimes.com/news/company/rssfeeds/2143429.cms",
//            "https://economictimes.indiatimes.com/news/defence/rssfeeds/46687796.cms",
//            "https://economictimes.indiatimes.com/news/international/rssfeeds/858478126.cms",
//            "https://economictimes.indiatimes.com/news/et-evoke/rssfeeds/79339235.cms",
//            "https://economictimes.indiatimes.com/news/elections/rssfeeds/65869819.cms",
//            "https://economictimes.indiatimes.com/news/sports/rssfeeds/26407562.cms",
//            "https://economictimes.indiatimes.com/news/science/rssfeeds/39872847.cms",
//            "https://economictimes.indiatimes.com/news/environment/rssfeeds/2647163.cms",
//            "https://economictimes.indiatimes.com/news/et-tv/rssfeedsvideo/48897386.cms"

    );

    @Async
    public void init() {
        if (running) {
            return;
        } else {
            running = true;
        }
        ITEM_SET.clear();

        RssReader rssReader = new RssReader();

        while (RUN_COUNT > 0) {
            LocalDateTime now = LocalDateTime.now();
            ZonedDateTime zonedDateTime = now.atZone(ZoneId.systemDefault()).minusHours(MAX_HOURS);

            URL_List.forEach(url -> {
                        try {
                            log.info(".");
                            Stream<Item> stream = rssReader.read(url);
                            Set<Item> set = stream.filter(item ->
                            {
                                if (item.getPubDate().isPresent()) {
                                    item.setPubDate(item.getPubDate().get().replace("+5:30", " +0530"));
                                    item.setPubDate(item.getPubDate().get().replace(" +05:30", " +0530"));
                                }
                                return item.getPubDateZonedDateTime().isPresent() && item.getPubDateZonedDateTime().get().isAfter(zonedDateTime);
                            }).filter(item -> item.getTitle().isPresent() && StringUtils.containsAnyIgnoreCase(item.getTitle().get(),
                                    KEYWORDS)).collect(Collectors.toSet());

                            if (set.size() > 0) {
                                playClip();
                                ITEM_SET.addAll(set);
                                ITEM_SET.stream().sorted(ItemComparator.newestItemFirst()).forEach(item -> {
                                    log.info("-------------------------------------------");
                                    log.info(item.getTitle().orElse("No Title"));
                                    log.info(item.getLink().orElse("No Link"));
                                    log.info("-------------------------------------------");
                                });
                            }

                            Thread.sleep(10 * 1000);

                        } catch (IOException | IllegalArgumentException | InterruptedException e) {
                            log.error("Ran into error for " + url, e);
                        }

                    }
            );
            log.info("X");
            RUN_COUNT--;
        }
        running = false;
    }

    private void playClip() {
        class AudioListener implements LineListener {
            private boolean done = false;

            @Override
            public synchronized void update(LineEvent event) {
                LineEvent.Type eventType = event.getType();
                if (eventType == LineEvent.Type.STOP || eventType == LineEvent.Type.CLOSE) {
                    done = true;
                    notifyAll();
                }
            }

            public synchronized void waitUntilDone() throws InterruptedException {
                while (!done) {
                    wait();
                }
            }
        }
        AudioListener listener = new AudioListener();
        try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(AUDIO_FILE)) {
            try (AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(Objects.requireNonNull(inputStream))) {
                try (Clip clip = AudioSystem.getClip()) {
                    clip.addLineListener(listener);
                    clip.open(audioInputStream);
                    clip.start();
                    listener.waitUntilDone();
                } catch (LineUnavailableException | InterruptedException e) {
                    log.error("",e);
                }
            } catch (UnsupportedAudioFileException e) {
                log.error("",e);
            }
        } catch (IOException e) {
            log.error("",e);
        }
    }

    public Set<Item> getData() {
        return ITEM_SET;
    }
}
