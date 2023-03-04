package com.example.demo;

import com.apptasticsoftware.rssreader.Item;
import com.apptasticsoftware.rssreader.RssReader;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class RSSReaderService {

    //Get the links from https://economictimes.indiatimes.com/rss.cms
    // Copied links till MF
    private static final Set<String> URL_List = Set.of(
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
//            "https://economictimes.indiatimes.com/news/et-tv/rssfeedsvideo/48897386.cms",
//            "https://economictimes.indiatimes.com/news/latest-news/rssfeeds/20989204.cms",
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
            "https://economictimes.indiatimes.com/rssfeedsdefault.cms",
            "https://economictimes.indiatimes.com/rssfeedstopstories.cms",
            "https://economictimes.indiatimes.com/prime/rssfeeds/69891145.cms",
            "https://economictimes.indiatimes.com/markets/rssfeeds/1977021501.cms",
            "https://economictimes.indiatimes.com/news/rssfeeds/1715249553.cms",
            "https://economictimes.indiatimes.com/industry/rssfeeds/13352306.cms",
            "https://economictimes.indiatimes.com/small-biz/rssfeeds/5575607.cms",
            "https://economictimes.indiatimes.com/wealth/rssfeeds/837555174.cms",
            "https://economictimes.indiatimes.com/mf/rssfeeds/359241701.cms",
            "https://economictimes.indiatimes.com/jobs/rssfeeds/107115.cms",
            "https://economictimes.indiatimes.com/opinion/rssfeeds/897228639.cms",
            "https://economictimes.indiatimes.com/nri/rssfeeds/7771250.cms",
            "https://economictimes.indiatimes.com/magazines/rssfeeds/1466318837.cms",
            "https://economictimes.indiatimes.com/podcasts/rssfeeds/70700695.cms"
    );

    private static final List<String> KEYWORD_LIST = List.of(
            "Tech Mahindra"
    );

    @PostConstruct
    public void init() {
        RssReader rssReader = new RssReader();
        Set<Item> set = new HashSet<>();
        URL_List.forEach(url -> {
                    try {
                        Stream<Item> stream = rssReader.read(url);
                        KEYWORD_LIST.forEach(keyword ->
                                set.addAll(stream.filter(item ->
                                        item.getTitle().isPresent() && item.getTitle().get().contains(keyword)
                                ).collect(Collectors.toSet()))
                        );
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
        );

        log.info(String.valueOf(set.size()));
    }

}
