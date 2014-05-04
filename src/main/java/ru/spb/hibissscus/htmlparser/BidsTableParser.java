package ru.spb.hibissscus.htmlparser;

//import ru.spb.hibiscus.beeline.common.utils.DateFormatter;
//import ru.spb.hibiscus.beeline.objects.type.BidType;
//import ru.spb.hibiscus.db.HibernateUtil;
//import ru.spb.hibiscus.db.RandomGUID;
//import ru.spb.hibiscus.db.dao.impl.BidDAO;
//import ru.spb.hibiscus.db.dao.impl.DistrictDAO;
//import ru.spb.hibiscus.db.entity.BidEntity;
//import ru.spb.hibiscus.db.entity.DistrictEntity;

/**
 * Парсер таблицы с заявками на день
 * использует jsoup
 */
public class BidsTableParser {

//    public static void main(String[] args) throws XMLStreamException, IOException {
//
//        try {
//
//            HlpdeskClient hlpdeskClient = new HlpdeskClient();
//
//            String bidsTableHTML = HlpdeskClient.getMethodExec("/shedule_nconnect.pl?area=348378&day=2011-05-11&step=3&ods=0");
//            /**
//             *  Получение списка заявок
//             */
//            ArrayList<BidEntity> bidEntities = parse(bidsTableHTML, "windows-1251", "http://hlpdesk.corbina.ru");
//            for (BidEntity entity : bidEntities) {
//                /**
//                 * Получение более подробных данных для заявки
//                 */
//                String bidHTML = HlpdeskClient.getMethodExec("/comments.pl?ticket_id=" + entity.getTicket());
//                parseBid(entity, bidHTML, "windows-1251", "http://hlpdesk.corbina.ru");
//            }
//
//        } catch (BidsError bidsError) {
//            bidsError.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        } catch (HlpdeskClient.HldError hldError) {
//            hldError.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        }
//
//        //final File file = new File(System.getProperty("user.dir") + "\\5.html");
//        //FileUtils.saveToFile(new File(System.getProperty("user.dir") + "\\1.html").getPath(),FileUtils.extractContents(file));
//
//        /**
//         *  Печать содержимого
//         */
//        //FileUtils.printFileBody(file, "windows-1251");
//
//        /**
//         *  Получение списка временных промежутков, выполнения заявок
//         */
////        ArrayList<String> arrayList = parseGetTimeTable(file, "windows-1251", "http://hlpdesk.corbina.ru");
////        for (String str : arrayList) {
////            System.out.println(str);
////        }
//
//
////        try {
////            /**
////             *  Получение списка заявок
////             */
////            parseBid(file, "windows-1251", "http://hlpdesk.corbina.ru");
////        } catch (Exception e) {
////            e.printStackTrace();
////        }
//    }
//
//    /**
//     * Получение списка заявок за конкретную дату и район
//     *
//     * @param date за какую дату запрашивается список заявок
//     * @param area район
//     */
//    public static void getAllBidsByDate(Date date, String area, String manager) {
//        try {
//
//            String ddmmyyyy = DateFormatter.toSqlDate(date).toString();
//
//
//            /**
//             * Определяем код присланного района
//             */
//            String areaCode = "";
//            List<DistrictEntity> districts = new DistrictDAO(HibernateUtil.openCacheSession()).findBy("districtName", area);
//            if (districts.size() > 0) {
//                areaCode = districts.get(0).getDistrictCode();
//            }
//
//            /**
//             *  Получение списка заявок
//             */
//            String bidsTableHTML = HlpdeskClient.getMethodExec("/shedule_nconnect.pl?area=" + areaCode + "&day=" + ddmmyyyy + "&step=3&ods=0");
//
//            ArrayList<BidEntity> bidEntities = parse(bidsTableHTML, "windows-1251", "http://hlpdesk.corbina.ru");
//            for (BidEntity entity : bidEntities) {
//                entity.setDate(new Timestamp(date.getTime()));
//                entity.setArea(area);
//                entity.setManager(manager);
//                /**
//                 * Получение более подробных данных для заявки
//                 */
//                String bidHTML = HlpdeskClient.getMethodExec("/comments.pl?ticket_id=" + entity.getTicket());
//                parseBid(entity, bidHTML, "windows-1251", "http://hlpdesk.corbina.ru");
//            }
//
//        } catch (BidsError bidsError) {
//            bidsError.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (XMLStreamException e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    /**
//     * Получение содержимого по ключу
//     * Пример:
//     * <td>Номер проблемы:</td>
//     * <td><b>109131044</b>
//     * </td>
//     * key Номер проблемы:
//     * return 109131044
//     *
//     * @param root Корневой элемент
//     * @param key  Текст который содержит запрашиваемый элемент
//     * @return Текстовое содержимое запрашиваемого элемента
//     */
//    public static String getContent(Element root, String key) {
//        try {
//            String result = "";
//            Elements elements = root.getElementsContainingOwnText(key);
//            if (!elements.text().equals("")) {
//                String tmp = elements.parents().first().text();
//
//                result = tmp.replace(key, "");
//                System.out.println(key + " " + result);
//                return result;
//            } else return result;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "";
//        }
//    }
//
//
//    /**
//     * Получение глубокого содержимого по ключу
//     * Пример:
//     * <tr>
//     * <td><b>Логины/CTN:</b></td><td><b>*0894668068 CTN:0894668068<br></td>
//     * </tr>
//     * key Логины/CTN:
//     * return *0894668068 CTN:0894668068
//     *
//     * @param root Корневой элемент
//     * @param key  Текст который содержит запрашиваемый элемент
//     * @return Текстовое содержимое запрашиваемого элемента
//     */
//    public static String getDeepContent(Element root, String key) {
//        try {
//            String result = "";
//            Elements elements = root.getElementsContainingOwnText(key);
//
//            //Получить имя тега - который содержит искомый текст
//            // с тем что бы поискать у родительского тега - содержимое всех подобных тегов
//            String tmp = "";
//            if (!elements.text().equals("")) {
//                String tagName = elements.first().tagName();
//                Element parent = elements.parents().get(1);
//                Elements allChildren = parent.getElementsByTag(tagName);
//                for (Element element : allChildren) {
//                    if (!element.text().equals("")) tmp = element.text();
//                }
//
//                result = tmp;
//                System.out.println(key + " " + result);
//                return result;
//            } else return result;
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "";
//        }
//    }
//
//    /**
//     * Разбор html файла с конкретной заявкой
//     *
//     * @param file     входной файл
//     * @param encoding кодировка
//     * @param baseURI  базовый URI
//     * @throws javax.xml.stream.XMLStreamException тип генерируемой ошибки
//     * @throws java.io.IOException        тип генерируемый ошибки
//     */
//    public static void parseBid(BidEntity bidEntity, String file, String encoding, String baseURI) throws XMLStreamException, IOException {
//
//        Document doc = Jsoup.parse(file, baseURI);
//        Elements forms = doc.getElementsByTag("form");
//
//        int findFirstForm = -1;
//        for (int i = 0; i < forms.size() && findFirstForm == -1; i++) {
//            Elements number = forms.get(i).getElementsContainingOwnText("Номер проблемы:");
//            if (!number.text().equals("")) {
//                findFirstForm = i;
//            }
//        }
//
//
//        if (findFirstForm != -1) {
//            Element root = forms.get(findFirstForm);
//            String number = getContent(root, "Номер проблемы:");
//            String type = getContent(root, "Тип:");
//            String address = getContent(root, "Адрес");
//            String client = getContent(root, "Клиент");
//            String contract = getDeepContent(root, "Контракт");
//            String phone = getContent(root, "Телефон(ы):");
//            String phone2 = getContent(root, "Контактные телефоны");
//            String login = getDeepContent(root, "Логины/CTN:");
//            String password = getDeepContent(root, "Пароль:");
//
//            bidEntity.setNumber(number);
//            String[] strs = address.split(",");
//            // Район записывается из комбобокса в фильтре заявки
//            bidEntity.setArea(strs[1]);
//            bidEntity.setStreet(strs[2]);
//            bidEntity.setHouse(strs[3]);
//            bidEntity.setApartment(strs[4]);
//            bidEntity.setClient(client);
//            bidEntity.setContract(contract);
//            bidEntity.setPhone(phone);
//            bidEntity.setLogin(login);
//            bidEntity.setPassword(password);
//
//            // добавляем комментарии
//
//            bidEntity.setComment("" +
//                    (bidEntity.getComment() != null ? (bidEntity.getComment() + "\n") : "") + phone2);
//        }
//
//        /**
//         * Сохраняем в базу
//         */
//        new BidDAO(HibernateUtil.openCacheSession()).save(bidEntity);
//
//    }
//
//    /**
//     * Получение списка временных промежутков, выполнения заявок
//     *
//     * @param file     входной файл
//     * @param encoding кодировка
//     * @param baseURI  базовый URI
//     * @return Список временных промежутков для заявок
//     * @throws javax.xml.stream.XMLStreamException тип генерируемой ошибки
//     * @throws java.io.IOException        тип генерируемый ошибки
//     */
//    public static ArrayList<String> parseGetTimeTable(String file, String encoding, String baseURI) throws XMLStreamException, IOException {
//        ArrayList<String> result = new ArrayList<String>();
//
//
//        StringBuilder stringBuilder = new StringBuilder("");
//
//        Document doc = Jsoup.parse(file, baseURI);
//
//        Elements forms = doc.getElementsByTag("form");
//
//        Elements links = forms.first().getElementsByTag("td");
//        for (Element link : links) {
//            Elements inputs = link.getElementsByTag("input");
//            stringBuilder = new StringBuilder("");
//            for (Element input : inputs) {
//                if (!stringBuilder.toString().equals("")) stringBuilder.append("-");
//                String inputAttr = input.attr("value");
//                if (!inputAttr.equals("")) {
//                    stringBuilder.append(inputAttr);
//                }
//                //System.out.println(inputAttr);
//            }
//            if (!stringBuilder.toString().equals(""))
//                result.add(stringBuilder.toString());
//        }
//
//
//        return result;
//    }
//
//
//    /**
//     * Получение списка заявок
//     *
//     * @param file     Входной html файл
//     * @param encoding кодировка
//     * @param baseURI  базовый URI
//     * @return Список упращённых заявок, для последующего разбора
//     * @throws javax.xml.stream.XMLStreamException
//     *                     тип генерируемой ошибки
//     * @throws java.io.IOException тип генерируемой ошибки
//     * @throws ru.spb.hibiscus.htmlparser.BidsTableParser.BidsError
//     *                     тип генерируемой ошибки
//     */
//    public static ArrayList<BidEntity> parse(String file, String encoding, String baseURI) throws XMLStreamException, IOException, BidsError {
//        ArrayList<BidEntity> bids = new ArrayList<BidEntity>();
//        BidEntity bid = null;
//
//        /**
//         * Получение списка временнЫх промежутков, выполнения заявок
//         */
//        ArrayList<String> timeTable = parseGetTimeTable(file, encoding, baseURI);
//
//
//        Document doc = Jsoup.parse(file, baseURI);
//        // Получение указателя на элемент form, в котором содержится список заявок на текущий день
//        Elements form = doc.getElementsByTag("form");
//        // Получить указатель на строки таблицы содержащие заявки
//        Elements trs = form.first().getElementsByTag("tr");
//        for (Element tr : trs) {
//            // Поулчить указатель на конкретную ячейку таблицы заявок
//            Elements tds = tr.getElementsByTag("td");
//            // Удалить первую ячейку содержащую номер строки
//            tds.remove(0);
//            // Проверить совпадает ли число ячеек в строке с числом временнЫх интервалов
//            if (tds.size() == timeTable.size()) {
//                // Пройтись по ячейкам строки
//                for (int i = 0; i < tds.size(); i++) {
//                    // Создаём простую заявку на подключения, которая будет участвовать в дальнейшем формировании
//                    // списка заявок на подключение
//                    bid = new BidEntity();
//                    bid.setId(new RandomGUID().toString());
//                    // Устанавливаем время для заявки
//                    bid.setTime(timeTable.get(i));
//                    // Получаем указатель на ссылку с более подробным содержимым заявки
//                    Elements a = tds.get(i).getElementsByTag("a");
//                    for (Element tdBid : a) {
//                        String bidHref = tdBid.attr("href");
//                        // Получение тикета заявки
//                        bid.setTicket(bidHref.substring(bidHref.indexOf("=") + 1));
//                        System.out.println(bidHref);
//                        bids.add(bid);
//                    }
//
//                    // Получаем указатель ip-tv или нет
//                    Elements iptv = tds.get(i).getElementsByTag("span");
//                    if (iptv.size() > 0) {
//                        bid.setType(BidType.IPTV.toString());
//                        bid.setComment("IPTV");
//                    }
//
//                    // Получаем содержимое - если это заявка на выдачу
//                    if (tds.get(i).text().contains("Доп.инф.:")) {
//                        Elements extend = tds.get(i).getElementsByTag("a");
//                        bid.setComment(extend.get(0).text());
//                        bid.setType(BidType.EXTENDED.toString());
//                    }
//                }
//
//                System.out.println("Следующая строка");
//            } else {
//                throw new BidsError("Число заявок не соответствует временным интервалам");
//            }
//        }
//
//        for (BidEntity bidEntity : bids) {
//            System.out.println(bidEntity);
//        }
//
//        return bids;
//    }

    /** Класс ошибок для парсера заявок на подключение */
    public static class BidsError extends Exception {
        public BidsError(String message) {
            super(message);
        }
    }
}
