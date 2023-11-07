package lotto.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lotto.domain.Lotto;
import lotto.port.LottoNumbersProvider;

public class LottoService {

    private static final int LOTTO_TICKET_PRICE = 1000;
    private static final String FIRST_ERROR_MESSAGE = "[ERROR] ";
    private static final String PURCHASE_AMOUNT_DIVIDE_EXCEPTION_MESSAGE = "로또 구입 금액은 1,000원으로 나누어 떨어져야합니다.";
    private static final String PURCHASE_AMOUNT_NOT_DIGIT_EXCEPTION_MESSAGE = "로또 구입 금액은 숫자여야합니다.";
    private static final String WINNING_NUMBERS_NOT_DIGIT_EXCEPTION_MESSAGE = "당첨 번호는 숫자여야합니다.";

    private final LottoNumbersProvider lottoNumbersProvider;

    public LottoService(LottoNumbersProvider lottoNumbersProvider) {
        this.lottoNumbersProvider = lottoNumbersProvider;
    }

    public int calculateNumberOfLottoTickets(String purchaseAmount) {
        validateLottoPurchaseAmount(purchaseAmount);
        return Integer.parseInt(purchaseAmount) / LOTTO_TICKET_PRICE;
    }

    public void validateLottoPurchaseAmount(String lottoPurchaseAmount) {

        if (PurchaseAmountNOTDivideBy1000(lottoPurchaseAmount)) {
            throw new IllegalArgumentException(
                FIRST_ERROR_MESSAGE + PURCHASE_AMOUNT_DIVIDE_EXCEPTION_MESSAGE);
        }

        if (PurchaseAmountNotDigit(lottoPurchaseAmount)) {
            throw new IllegalArgumentException(
                FIRST_ERROR_MESSAGE + PURCHASE_AMOUNT_NOT_DIGIT_EXCEPTION_MESSAGE);
        }
    }

    private boolean PurchaseAmountNOTDivideBy1000(String lottoPurchaseAmount) {
        int amount = Integer.parseInt(lottoPurchaseAmount);
        return amount % LOTTO_TICKET_PRICE != 0;
    }

    private boolean PurchaseAmountNotDigit(String lottoPurchaseAmount) {
        try {
            Integer.parseInt(lottoPurchaseAmount);
            return false;
        } catch (NumberFormatException e) {
            return true;
        }
    }

    public List<List<Integer>> generateLottoNumbers(int lottoTicketsCount) {
        List<List<Integer>> allLottoNumbers = new ArrayList<>();

        System.out.println(lottoTicketsCount + "개를 구매했습니다.");

        for (int i = 0; i < lottoTicketsCount; i++) {
            List<Integer> lottoNumbers = lottoNumbersProvider.provideLottoNumbers();
            Collections.sort(lottoNumbers);
            allLottoNumbers.add(lottoNumbers);
            System.out.println(lottoNumbers);
        }
        return allLottoNumbers;
    }

    public void addLottoNumberToWinningNumbers(String lottoNumbersString) {
        List<Integer> winningNumbers = convertStringToWinningNumbers(lottoNumbersString);
        Lotto lotto = new Lotto(winningNumbers);
    }


    public List<Integer> convertStringToWinningNumbers(String lottoNumbersString) {
        String[] splitNumbers = lottoNumbersString.split(",");
        List<Integer> lottoNumbers = new ArrayList<>();
        for (String number : splitNumbers) {
            try {
                lottoNumbers.add(Integer.parseInt(number.trim()));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(FIRST_ERROR_MESSAGE + WINNING_NUMBERS_NOT_DIGIT_EXCEPTION_MESSAGE);
            }
        }
        return lottoNumbers;
    }
}